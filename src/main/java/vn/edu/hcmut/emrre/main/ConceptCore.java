package vn.edu.hcmut.emrre.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.xalan.xsltc.ProcessorVersion;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.common.Constant;
import vn.edu.hcmut.emrre.common.Dictionary;
import vn.edu.hcmut.emrre.common.ReadFile;
import vn.edu.hcmut.emrre.common.WordHandle;
import vn.edu.hcmut.emrre.common.WriteFile;
import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.entity.sentence.SentenceDAO;
import vn.edu.hcmut.emrre.core.entity.sentence.SentenceDAOImpl;
import vn.edu.hcmut.emrre.core.entity.word.Word;
import vn.edu.hcmut.emrre.core.feature.ConceptFeatureVn;
import vn.edu.hcmut.emrre.core.preprocess.ProcessText;
import vn.edu.hcmut.emrre.core.preprocess.ProcessVNText;
import vn.edu.hcmut.emrre.core.svm.SVM;
import vn.edu.hcmut.emrre.modal.entity.Concept;
import vn.edu.hcmut.emrre.modal.entity.DocLine;
import vn.edu.hcmut.emrre.modal.entity.Relation;

public class ConceptCore {
	private static List<int[]> BIOs;
	private static List<Concept> concepts;
	private static List<DocLine> doclines;
	private ConceptFeatureVn featureExtractor;

	public ConceptCore() {
		featureExtractor = new ConceptFeatureVn();
	}

	public static List<DocLine> getDoclines() {
		return doclines;
	}

	public static List<Concept> getConcepts() {
		return concepts;
	}

	public static void setConcepts(List<Concept> concepts) {
		ConceptCore.concepts = concepts;
	}

	private void getConceptData() {
		if (ConceptCore.concepts == null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/concept");
			ConceptCore.concepts = read.getAllConcept(0);
		}
	}

	private void getDoclineData() {
		if (doclines == null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/txt");
			doclines = read.getAllDocLine();
		}
	}

	private int[] initVector(int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = Constant.BIO_O;
		}
		return result;
	}

	private int convert2BIO(Concept.Type type, boolean begin) {
		if (type == Concept.Type.PROBLEM) {
			return (begin) ? Constant.BIO_B_PR : Constant.BIO_I_PR;
		}
		if (type == Concept.Type.TEST) {
			return (begin) ? Constant.BIO_B_TE : Constant.BIO_I_TE;
		}
		return (begin) ? Constant.BIO_B_TR : Constant.BIO_I_TR;
	}
	
	private Concept.Type convert2Type(int bio) {
		switch (bio) {
		case Constant.BIO_B_PR:
			return Concept.Type.PROBLEM;
		case Constant.BIO_B_TE:
			return Concept.Type.TEST;
		case Constant.BIO_B_TR:
			return Concept.Type.TREATMENT;
		default:
			return null;
		}
	}

	// private List<Concept> convert2Concept(DocLine doc, int[] bio) {
	//
	// }

	private List<Concept> getDoclineConcepts(String fileName, int lineIdx) {
		List<Concept> result = new ArrayList<Concept>();
		for (Concept concept : ConceptCore.concepts) {
			if (concept.getFileName().equals(fileName) && concept.getLine() == lineIdx) {
				result.add(concept);
			}
		}
		return result;
	}

	private void generateBIO() {
		int[] vector;
		int idx;
		DocLine doc;
		String[] lstWord;
		List<Concept> lstConcept;
		if (ConceptCore.doclines == null) {
			return;
		}
		BIOs = new ArrayList<int[]>();
		for (idx = 0; idx < ConceptCore.doclines.size(); idx++) {
			doc = ConceptCore.doclines.get(idx);
			lstWord = WordHandle.tokenizeSentence(doc.getContent());
			// init vector
			vector = initVector(lstWord.length);
			lstConcept = getDoclineConcepts(doc.getFileName(), doc.getLineIndex());
			for (Concept concept : lstConcept) {
				vector[concept.getBegin() - 1] = convert2BIO(concept.getType(), true);
				for (int i = concept.getBegin() + 1; i <= concept.getEnd(); i++) {
					vector[i - 1] = convert2BIO(concept.getType(), false);
				}
			}
			BIOs.add(vector);
		}
	}

	private boolean isBegCon(int bio) {
		return bio == Constant.BIO_B_PR || bio == Constant.BIO_B_TE || bio == Constant.BIO_B_TR;
	}

	private boolean isInCon(int bio) {
		return bio == Constant.BIO_I_PR || bio == Constant.BIO_I_TE || bio == Constant.BIO_I_TR;
	}

	/*
	 * @method: statistics
	 * 
	 * @return: int[3]: correct, predict, predict correctly
	 */
	private int[] statistics(int[] corVec, int[] preVec, int[][] detail) {
		int[] result = new int[3];
		if (corVec != null) {
			for (int i = 0; i < corVec.length; i++) {
				if (isBegCon(corVec[i])) {
					result[0]++;
					detail[corVec[i] - 1][0]++; /* correct */
				}
			}
		}
		if (preVec != null) {
			for (int i = 0; i < preVec.length; i++) {
				if (isBegCon(preVec[i])) {
					result[1]++;
					detail[preVec[i] - 1][1]++; /* predict */
				}
			}
		}
		if (corVec != null && preVec != null) {
			for (int i = 0; i < corVec.length;) {
				if (isBegCon(preVec[i]) && preVec[i] == corVec[i]) {
					boolean kt = true;
					int j = i + 1;
					while (j < corVec.length && (isInCon(preVec[j]) || isInCon(corVec[j]))) {
						if (corVec[j] != preVec[j]) {
							kt = false;
							break;
						}
						j++;
					}
					if (kt) {
						result[2]++;
						detail[preVec[i] - 1][2]++; /* predict correctly */
					}
					i = j;
				} else {
					i++;
				}
			}
		}
		return result;
	}

	// dataTest: [0, 1, 1, 0, 2, 0] -> result: [1, 2, 4, 1, 1, 2]
	private double[] preProcess(double[] dataTest) {
		double[] result = null;
		int size = 0;
		for (double ele : dataTest) {
			if (ele > 0)
				size++;
		}
		if (size > 0) {
			result = new double[size * 2];
			int idx = 0;
			for (int i = 0; i < dataTest.length; i++) {
				if (dataTest[i] > 0) {
					result[idx + size] = dataTest[i];
					result[idx++] = i + 1;
				}
			}
		}
		return result;
	}

	public void crossValidation(int numFold) throws IOException, InvalidInputDataException {
		getConceptData();
		getDoclineData();
		generateBIO();
		SVM svm = new SVM(Constant.CONCEPT_MODEL_FILE_PATH_TRAIN);
		SentenceDAO senDao = new SentenceDAOImpl();
		Sentence sentence;
		int[][] detail = new int[][] { { 0, 0, 0 }, { 0, 0, 0 },
				{ 0, 0, 0 } }; /* 0: problem, 1: test, 2: treatment */
		int correctTt = 0, total = 0, predict = 0;
		for (int fold = 0; fold < numFold; fold++) {
			/* preprocess */
			Dictionary conceptDic = new Dictionary();
			Dictionary wordBeside = new Dictionary();
			for (int i = 0; i < doclines.size(); i++) {
				if (i % numFold != fold) {
					DocLine doc = doclines.get(i);
					List<Concept> lstConcept = getDoclineConcepts(doc.getFileName(), doc.getLineIndex());
					String cContent;
					for (Concept concept : lstConcept) {
						for (int pos = concept.getBegin() - 1; pos < concept.getEnd(); pos++) {
							cContent = WordHandle.getWord(doc.getContent(), pos);
							if (cContent != null) {
								conceptDic.addDictionary(cContent.toLowerCase());
							}
						}
						for (int pos = concept.getBegin() - 3; pos < concept.getBegin(); pos++) {
							cContent = WordHandle.getWord(doc.getContent(), pos - 1);
							if (cContent != null) {
								wordBeside.addDictionary(cContent.toLowerCase());
							}
						}
						for (int pos = concept.getEnd() + 1; pos <= concept.getEnd() + 3; pos++) {
							cContent = WordHandle.getWord(doc.getContent(), pos - 1);
							if (cContent != null) {
								wordBeside.addDictionary(cContent.toLowerCase());
							}
						}
					}
				}
			}
			conceptDic.saveDictionary2File(Constant.CONCEPT_CONCEPT_DICTIONARY_FILE_PATH);
			wordBeside.saveDictionary2File(Constant.CONCEPT_WORD_DICTIONARY_FILE_PATH);

			/* train */
			WriteFile wf = new WriteFile(Constant.CONCEPT_DATA_TRAIN_FILE_PATH);
			wf.open(false);
			for (int i = 0; i < doclines.size(); i++) {
				if (i % numFold != fold) {
					DocLine doc = doclines.get(i);
					sentence = senDao.findByRecordAndLineIndex(doc.getFileName(), doc.getLineIndex());
					if (sentence != null) {
						featureExtractor.setWordLst(sentence.getWords());
					}
					String[] lstWord = WordHandle.tokenizeSentence(doc.getContent());
					int[] bio = BIOs.get(i);
					featureExtractor.setLstWord(lstWord);
					featureExtractor.setBio(bio);
					for (int k = 0; k < bio.length; k++) {
						double[] vector = featureExtractor.buildFeatures(k);
						wf.write("" + bio[k]);
						for (int j = 0; j < vector.length; j++) {
							if (vector[j] != 0) {
								wf.write(" " + (j + 1) + ":" + vector[j]);
							}
						}
						wf.writeln("");
					}
				}
			}
			wf.close();
			svm.svmTrainCore(new File(Constant.CONCEPT_DATA_TRAIN_FILE_PATH));

			// test
			for (int i = 0; i < doclines.size(); i++) {
				if (i % numFold == fold) {
					DocLine doc = doclines.get(i);
					sentence = senDao.findByRecordAndLineIndex(doc.getFileName(), doc.getLineIndex());
					if (sentence != null) {
						featureExtractor.setWordLst(sentence.getWords());
					}
					String[] lstWord = WordHandle.tokenizeSentence(doc.getContent());
					int[] bio = BIOs.get(i);
					int[] predictVector = new int[bio.length];
					featureExtractor.setLstWord(lstWord);
					for (int k = 0; k < bio.length; k++) {
						featureExtractor.setBio(predictVector);
						double[] vector = featureExtractor.buildFeatures(k);
						vector = preProcess(vector);
						if (vector != null) {
							predictVector[k] = (int) svm.svmTestCore(vector, true);
						}
					}
					int[] sta = statistics(bio, predictVector, detail);
					if (sta[2] > 0) {
					} else {
					}
					total += sta[0];
					predict += sta[1];
					correctTt += sta[2];
				}
			}
		}
		System.out.println("Correct: " + correctTt + ", total: " + total + ", predict: " + predict);
		double precision = (double) correctTt / predict * 100;
		double recall = (double) correctTt / total * 100;
		double f1 = 2 * precision * recall / (precision + recall);
		System.out.println(String.format("Precision = %f%%", precision));
		System.out.println(String.format("Recall = %f%%", recall));
		System.out.println(String.format("F1 = %f%%", f1));
		System.out.println("Detail: ");
		System.out.println(
				"Problem: correct: " + detail[0][2] + ", total: " + detail[0][0] + ", predict: " + detail[0][1]);
		System.out
				.println("Test: correct: " + detail[1][2] + ", total: " + detail[1][0] + ", predict: " + detail[1][1]);
		System.out.println(
				"Treatment: correct: " + detail[2][2] + ", total: " + detail[2][0] + ", predict: " + detail[2][1]);
	}

	public void generateModel() throws IOException, InvalidInputDataException {
		getConceptData();
		getDoclineData();
		generateBIO();
		SVM svm = new SVM(Constant.CONCEPT_MODEL_FILE_PATH);
		SentenceDAO senDao = new SentenceDAOImpl();
		Sentence sentence;
		/* preprocess */
		Dictionary conceptDic = new Dictionary();
		Dictionary wordBeside = new Dictionary();
		for (int i = 0; i < doclines.size(); i++) {
			DocLine doc = doclines.get(i);
			List<Concept> lstConcept = getDoclineConcepts(doc.getFileName(), doc.getLineIndex());
			String cContent;
			for (Concept concept : lstConcept) {
				for (int pos = concept.getBegin() - 1; pos < concept.getEnd(); pos++) {
					cContent = WordHandle.getWord(doc.getContent(), pos);
					if (cContent != null) {
						conceptDic.addDictionary(cContent.toLowerCase());
					}
				}
				for (int pos = concept.getBegin() - 3; pos < concept.getBegin(); pos++) {
					cContent = WordHandle.getWord(doc.getContent(), pos - 1);
					if (cContent != null) {
						wordBeside.addDictionary(cContent.toLowerCase());
					}
				}
				for (int pos = concept.getEnd() + 1; pos <= concept.getEnd() + 3; pos++) {
					cContent = WordHandle.getWord(doc.getContent(), pos - 1);
					if (cContent != null) {
						wordBeside.addDictionary(cContent.toLowerCase());
					}
				}
			}
		}
		conceptDic.saveDictionary2File(Constant.CONCEPT_CONCEPT_DICTIONARY_FILE_PATH);
		wordBeside.saveDictionary2File(Constant.CONCEPT_WORD_DICTIONARY_FILE_PATH);

		/* train */
		WriteFile wf = new WriteFile(Constant.CONCEPT_DATA_TRAIN_FILE_PATH);
		wf.open(false);
		for (int i = 0; i < doclines.size(); i++) {
			DocLine doc = doclines.get(i);
			sentence = senDao.findByRecordAndLineIndex(doc.getFileName(), doc.getLineIndex());
			if (sentence != null) {
				featureExtractor.setWordLst(sentence.getWords());
			}
			String[] lstWord = WordHandle.tokenizeSentence(doc.getContent());
			int[] bio = BIOs.get(i);
			featureExtractor.setLstWord(lstWord);
			featureExtractor.setBio(bio);
			for (int k = 0; k < bio.length; k++) {
				double[] vector = featureExtractor.buildFeatures(k);
				wf.write("" + bio[k]);
				for (int j = 0; j < vector.length; j++) {
					if (vector[j] != 0) {
						wf.write(" " + (j + 1) + ":" + vector[j]);
					}
				}
				wf.writeln("");
			}
		}
		wf.close();
		svm.svmTrainCore(new File(Constant.CONCEPT_DATA_TRAIN_FILE_PATH));
	}

	public void test(String text) throws IOException {
		ProcessText pt = ProcessVNText.getInstance();
		List<Sentence> SenLst = pt.processDocument(text, false);
		List<Concept> lstConcept = extractConcept(SenLst);
		Sentence sentence;
		for (int idx = 0; idx < SenLst.size(); idx ++) {
			sentence = SenLst.get(idx);
			System.out.println("sentence is :" + sentence.getContent());
			System.out.println("");
			for (Concept con : lstConcept) {
				if (con.getLine() == idx + 1) {
					System.out.println(con.toString());
				}
			}
		}
	}

	public List<Concept> extractConcept(List<Sentence> SenLst) throws IOException {
		if (SenLst == null) {
			return null;
		}
		List<Concept> result = new ArrayList<Concept>();
		SVM svm = new SVM(Constant.CONCEPT_MODEL_FILE_PATH);
		Sentence sentence;
		for (int idx = 0; idx < SenLst.size(); idx ++) {
			sentence = SenLst.get(idx);
			featureExtractor.setWordLst(sentence.getWords());
			String[] lstWord = WordHandle.tokenizeSentence(sentence.getContent());
			int[] predictVector = new int[lstWord.length];
			featureExtractor.setLstWord(lstWord);
			// test
			for (int k = 0; k < predictVector.length; k++) {
				featureExtractor.setBio(predictVector);
				double[] vector = featureExtractor.buildFeatures(k);
				vector = preProcess(vector);
				if (vector != null) {
					predictVector[k] = (int) svm.svmTestCore(vector, true);
					System.out.println(predictVector[k]);
				}
			}
			//convert bio -> concept: begin, end, content, type
			Concept concept;
			String content;
			int begin, end;
			Concept.Type type;
			for (int k = 0; k < predictVector.length;) {
				if (isBegCon(predictVector[k])) {
					begin = k;
					content = lstWord[k];
					type = convert2Type(predictVector[k]);
					k = k + 1;
					while (k < predictVector.length && isInCon(predictVector[k])) {
						content += " " + lstWord[k];
						k ++;
					}
					end = k - 1;
					concept = new Concept(null, content, idx + 1, begin + 1, end + 1, type, 0);
					result.add(concept);
				}
				else {
					k ++;
				}
			}
		}
		return result;
	}

	public void test() throws IOException {
		getConceptData();
		getDoclineData();
		for (int i = 0; i < 10; i++) {
			DocLine doc = doclines.get(i);
			System.out.println(doc.getFileName() + " " + doc.getLineIndex());
			List<Concept> lstConcept = getDoclineConcepts(doc.getFileName(), doc.getLineIndex());
			for (Concept concept: lstConcept) {
				System.out.println(concept.getContent());
				System.out.println(WordHandle.getWords(doc.getContent(), concept.getBegin() - 1, concept.getEnd() - 1));
			}
		}
	}
}
