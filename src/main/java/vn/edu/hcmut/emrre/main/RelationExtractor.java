package vn.edu.hcmut.emrre.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.NLP.ProcessText;
import vn.edu.hcmut.emrre.NLP.ProcessVNText;
import vn.edu.hcmut.emrre.common.Constant;
import vn.edu.hcmut.emrre.common.Dictionary;
import vn.edu.hcmut.emrre.common.ReadFile;
import vn.edu.hcmut.emrre.common.WordHandle;
import vn.edu.hcmut.emrre.common.WriteFile;
import vn.edu.hcmut.emrre.entity.Concept;
import vn.edu.hcmut.emrre.entity.DocLine;
import vn.edu.hcmut.emrre.entity.Relation;
import vn.edu.hcmut.emrre.entity.Sentence;
import vn.edu.hcmut.emrre.feature.RelationFeatureVn;
import vn.edu.hcmut.emrre.machineLearningMethod.SVM;

public class RelationExtractor {
	private static List<Relation> relations;
	private static List<Concept> concepts;
	private static List<DocLine> doclines;
	private RelationFeatureVn featureExtractor;
	private List<Concept> conceptLstOut;
	private List<Sentence> sentenceLstOut;

	public List<Concept> getConceptLstOut() {
		return conceptLstOut;
	}

	public List<Sentence> getSentenceLstOut() {
		return sentenceLstOut;
	}

	public RelationExtractor() {
		featureExtractor = new RelationFeatureVn();
	}

	public static List<Relation> getRelations() {
		return relations;
	}

	public static void setRelations(List<Relation> relations) {
		RelationExtractor.relations = relations;
	}

	public static List<DocLine> getDoclines() {
		return doclines;
	}

	public static List<Concept> getConcepts() {
		return concepts;
	}

	public static void setConcepts(List<Concept> concepts) {
		RelationExtractor.concepts = concepts;
	}

	public void getConceptData() {
		if (RelationExtractor.concepts == null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/concept");
			RelationExtractor.concepts = read.getAllConcept(0);
		}
	}

	public void getRelationData() {
		if (RelationExtractor.relations == null && RelationExtractor.concepts != null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/rel");
			RelationExtractor.relations = read.getAllRelation(concepts, true);
		}
	}

	public void getDoclineData() {
		if (doclines == null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/txt");
			doclines = read.getAllDocLine();
		}
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
		getRelationData();
		getDoclineData();
		featureExtractor.setLstConcept(concepts);
		SVM svm = new SVM(Constant.MODEL_FILE_PATH_CROSS);
		int noneTt = 0, nonePre = 0, noneTo = 0;
		int correctTt = 0, total = 0, predict = 0;
		int[][] result = new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 },
				{ 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
		for (int fold = 0; fold < numFold; fold++) {
			// preprocess
			Dictionary conceptDic = new Dictionary();
			Dictionary wordBetween = new Dictionary();
			for (int i = 0; i < relations.size(); i++) {
				if (i % numFold != fold) {
					Relation rel = relations.get(i);
					Concept preConcept = rel.getPreConcept();
					Concept posConcept = rel.getPosConcept();
					String cContent = WordHandle.getWords(rel.getFileName(), preConcept.getLine(),
							preConcept.getBegin(), preConcept.getEnd());
					if (cContent != null) {
						conceptDic.addDictionary(cContent);
					}
					cContent = WordHandle.getWords(rel.getFileName(), posConcept.getLine(), posConcept.getBegin(),
							posConcept.getEnd());
					if (cContent != null) {
						conceptDic.addDictionary(cContent);
					}

					for (int pos = preConcept.getEnd() + 1; pos < posConcept.getBegin(); pos++) {
						cContent = WordHandle.getWord(rel.getFileName(), preConcept.getLine(), pos);
						if (cContent != null) {
							wordBetween.addDictionary(cContent);
						}
					}
				}
			}
			conceptDic.saveDictionary2File(Constant.CONCEPT_DICTIONARY_FILE_PATH);
			wordBetween.saveDictionary2File(Constant.WORD_DICTIONARY_FILE_PATH);

			// train
			WriteFile wf = new WriteFile(Constant.DATA_TRAIN_FILE_PATH);
			wf.open(false);
			for (int i = 0; i < relations.size(); i++) {
				if (i % numFold != fold) {
					Relation rel = relations.get(i);
					Concept first = rel.getPreConcept();
					Concept second = rel.getPosConcept();
					double[] vector = featureExtractor.buildFeatures(first, second);
					// if (vectorValid(vector)){
					wf.write("" + (int) rel.getType().getValue());
					for (int j = 0; j < vector.length; j++) {
						if (vector[j] != 0) {
							wf.write(" " + (j + 1) + ":" + vector[j]);
						}
					}
					wf.writeln("");
					// }
				}
			}
			wf.close();
			svm.svmTrainCore(new File(Constant.DATA_TRAIN_FILE_PATH));

			// test
			for (int i = 0; i < relations.size(); i++) {
				if (i % numFold == fold) {
					Relation rel = relations.get(i);
					result[rel.getType().getValue()][0]++;
					if (rel.getType().getValue() != 0)
						total++;
					Concept first = rel.getPreConcept();
					Concept second = rel.getPosConcept();
					double[] vector = featureExtractor.buildFeatures(first, second);
					vector = preProcess(vector);
					if (vector != null) {
						int label = (int) svm.svmTestCore(vector, true);
						if (label != 0)
							predict++;
						result[label][1]++;
						if (rel.getType().getValue() != 0 && label != 0) 
							noneTt ++;
						if (label == rel.getType().getValue()) {
							result[label][2]++;
							System.out.println("Correct " + label);
							if (label != 0)
								correctTt++;
						}
					}
				}
			}
		}

		// print
		for (int i = 0; i < 9; i++) {
			System.out.println(String.format("%s: %d -- predict: %d -- correct: %d", Relation.typeOfDouble(i),
					result[i][0], result[i][1], result[i][2]));
		}
		double precision = (double) correctTt / predict * 100;
		double recall = (double) correctTt / total * 100;
		double f1 = 2 * precision * recall / (precision + recall);
		System.out.println(String.format("total: %d -- predict: %d -- correct: %d", total, predict, correctTt));
		System.out.println(String.format("Precision = %f%%", precision));
		System.out.println(String.format("Recall = %f%%", recall));
		System.out.println(String.format("F1 = %f%%", f1));
		
		precision = (double) noneTt / predict * 100;
		recall = (double) noneTt / total * 100;
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println(String.format("total: %d -- predict: %d -- correct: %d", total, predict, correctTt));
		System.out.println(String.format("Precision = %f%%", precision));
		System.out.println(String.format("Recall = %f%%", recall));
		System.out.println(String.format("F1 = %f%%", f1));
	}

	public void generateModel() throws IOException, InvalidInputDataException {
		getConceptData();
		getRelationData();
		getDoclineData();
		featureExtractor.setLstConcept(concepts);
		SVM svm = new SVM(Constant.MODEL_FILE_PATH);

		// preprocess
		Dictionary conceptDic = new Dictionary();
		Dictionary wordBetween = new Dictionary();
		for (int i = 0; i < relations.size(); i++) {
			Relation rel = relations.get(i);
			Concept preConcept = rel.getPreConcept();
			Concept posConcept = rel.getPosConcept();
			String cContent = WordHandle.getWords(rel.getFileName(), preConcept.getLine(), preConcept.getBegin(),
					preConcept.getEnd());
			if (cContent != null) {
				conceptDic.addDictionary(cContent);
			}
			cContent = WordHandle.getWords(rel.getFileName(), posConcept.getLine(), posConcept.getBegin(),
					posConcept.getEnd());
			if (cContent != null) {
				conceptDic.addDictionary(cContent);
			}

			for (int pos = preConcept.getEnd() + 1; pos < posConcept.getBegin(); pos++) {
				cContent = WordHandle.getWord(rel.getFileName(), preConcept.getLine(), pos);
				if (cContent != null) {
					wordBetween.addDictionary(cContent);
				}
			}
			conceptDic.saveDictionary2File(Constant.CONCEPT_DICTIONARY_FILE_PATH);
			wordBetween.saveDictionary2File(Constant.WORD_DICTIONARY_FILE_PATH);
		}

		// train
		WriteFile wf = new WriteFile(Constant.DATA_TRAIN_FILE_PATH);
		wf.open(false);
		for (int i = 0; i < relations.size(); i++) {
			Relation rel = relations.get(i);
			Concept first = rel.getPreConcept();
			Concept second = rel.getPosConcept();
			double[] vector = featureExtractor.buildFeatures(first, second);
			wf.write("" + (int) rel.getType().getValue());
			for (int j = 0; j < vector.length; j++) {
				if (vector[j] != 0) {
					wf.write(" " + (j + 1) + ":" + vector[j]);
				}
			}
			wf.writeln("");
		}
		wf.close();
		svm.svmTrainCore(new File(Constant.DATA_TRAIN_FILE_PATH));
	}

	/*
	 * @function: extractRelation
	 * 
	 * @description: extract relations from list of sentences and list of
	 * concepts
	 * 
	 * @parameters: senLst: list of sentences (input) concepts: list of concepts
	 * (input)
	 * 
	 * @return: null if senLst = null or concepts = null list of relations
	 */
	public List<Relation> extractRelation(List<Sentence> senLst, List<Concept> concepts) throws IOException {
		this.sentenceLstOut = senLst;
		this.conceptLstOut = concepts;
		// SVM to predict
		if (senLst == null || concepts == null) {
			return null;
		}
		SVM svm = new SVM(Constant.MODEL_FILE_PATH);
		List<Relation> relLst = new ArrayList<Relation>();
		for (int idx = 0; idx < senLst.size(); idx++) {
			Sentence sentence = senLst.get(idx);
			featureExtractor.setPredict(true);
			featureExtractor.setSentence(sentence);
			featureExtractor.setLstWord(sentence.getWords());
			featureExtractor.setLstConcept(concepts);
			// get vector
			for (int i = 0; i < concepts.size(); i++)
				for (int j = i + 1; j < concepts.size(); j++) {
					if (Relation.canRelate(concepts.get(i), concepts.get(j)) && concepts.get(i).getLine() == idx + 1
							&& concepts.get(j).getLine() == idx + 1) {
						double[] vector;
						Concept pre, post;
						Relation.Type type;
						if (concepts.get(i).getBegin() < concepts.get(j).getBegin()) {
							pre = concepts.get(i);
							post = concepts.get(j);
						} else {
							pre = concepts.get(j);
							post = concepts.get(i);
						}
						vector = featureExtractor.buildFeatures(pre, post);
						vector = preProcess(vector);
						if (vector != null) {
							int label = (int) svm.svmTestCore(vector, true);
							type = Relation.typeOfDouble(label);
							if (type != Relation.Type.NONE) {
								Relation rel = new Relation(pre, post, type, 0);
								relLst.add(rel);
							}
						}
					}
				}
		}
		return relLst;
	}

	/*
	 * @function: extractRelation
	 * 
	 * @description: full pipeline for extracting relations
	 * 
	 * @parameters: input: text (input)
	 * 
	 * @return: list of relations
	 */
	public List<Relation> extractRelation(String input) throws IOException {
		ConceptExtractor cc = new ConceptExtractor();
		RelationExtractor rc = new RelationExtractor();
		ProcessText pt = ProcessVNText.getInstance();
		this.sentenceLstOut = pt.processDocument(input, false);
		this.conceptLstOut = cc.extractConcept(sentenceLstOut);
		List<Relation> relLst = rc.extractRelation(sentenceLstOut, conceptLstOut);
		return relLst;
	}
}
