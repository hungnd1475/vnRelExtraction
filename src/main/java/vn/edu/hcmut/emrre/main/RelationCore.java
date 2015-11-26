package vn.edu.hcmut.emrre.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.DocLine;
import vn.edu.hcmut.emrre.core.entity.Relation;
import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.feature.RelationFeatureVn;
import vn.edu.hcmut.emrre.core.preprocess.ProcessText;
import vn.edu.hcmut.emrre.core.preprocess.ProcessVNText;
import vn.edu.hcmut.emrre.core.svm.SVM;
import vn.edu.hcmut.emrre.core.utils.Constant;
import vn.edu.hcmut.emrre.core.utils.Dictionary;
import vn.edu.hcmut.emrre.core.utils.ReadFile;
import vn.edu.hcmut.emrre.core.utils.WordHandle;
import vn.edu.hcmut.emrre.core.utils.WriteFile;

public class RelationCore {
	private static List<Relation> relations;
	private static List<Concept> concepts;
	private static List<DocLine> doclines;
	private RelationFeatureVn featureExtractor;

	public RelationCore() {
		featureExtractor = new RelationFeatureVn();
	}

	public static List<Relation> getRelations() {
		return relations;
	}

	public static void setRelations(List<Relation> relations) {
		RelationCore.relations = relations;
	}

	public static List<DocLine> getDoclines() {
		return doclines;
	}

	public static List<Concept> getConcepts() {
		return concepts;
	}

	public static void setConcepts(List<Concept> concepts) {
		RelationCore.concepts = concepts;
	}

	public void getConceptData() {
		if (RelationCore.concepts == null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/concept");
			RelationCore.concepts = read.getAllConcept(0);
		}
	}

	public void getRelationData() {
		if (RelationCore.relations == null && RelationCore.concepts != null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/rel");
			RelationCore.relations = read.getAllRelation(concepts, true);
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
					String cContent = WordHandle.getWords(rel.getFileName(), preConcept.getLine(), preConcept.getBegin(),
							preConcept.getEnd());
					if (cContent != null) {
						conceptDic.addDictionary(cContent);
					}
					cContent = WordHandle.getWords(rel.getFileName(), posConcept.getLine(), posConcept.getBegin(), posConcept.getEnd());
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
		
		//print
		for (int i = 0; i < 9; i++) {
			System.out.println(
					String.format("%s: %d -- predict: %d -- correct: %d", Relation.typeOfDouble(i), result[i][0], result[i][1], result[i][2]));
		}
		double precision = (double) correctTt / predict * 100;
		double recall = (double) correctTt / total * 100;
		double f1 = 2 * precision * recall / (precision + recall);
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
			cContent = WordHandle.getWords(rel.getFileName(), posConcept.getLine(), posConcept.getBegin(), posConcept.getEnd());
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
	 * @description: extract relations from list of sentences and list of concepts
	 * @parameters: senLst: list of sentences (input)
	 * 				concepts: list of concepts (input)
	 * @return: null if senLst = null or concepts = null
	 * 			list of relations
	 */
	public List<Relation> extractRelation(List<Sentence> senLst, List<Concept> concepts) throws IOException {
		//SVM to predict
		if (senLst == null || concepts == null) {
			return null;
		}
		SVM svm = new SVM(Constant.MODEL_FILE_PATH);
		List<Relation> relLst = new ArrayList<Relation>();
		for (Sentence sentence : senLst) {
			featureExtractor.setPredict(true);
			featureExtractor.setSentence(sentence);
			featureExtractor.setLstWord(sentence.getWords());
			featureExtractor.setLstConcept(concepts);
			//get vector
			for (int i = 0; i < concepts.size(); i ++)
				for (int j = i + 1; j <concepts.size(); j ++) {
					if (Relation.canRelate(concepts.get(i), concepts.get(j))) {
						double[] vector;
						Concept pre, post;
						Relation.Type type;
						if (concepts.get(i).getBegin() < concepts.get(j).getBegin()) {
							pre = concepts.get(i);
							post = concepts.get(j);
						}
						else {
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
	 * @description: full pipeline for extracting relations 
	 * @parameters: input: text (input)
	 *				conceptLstOut: list concepts (output)
	 *				sentenceLstOut: list sentences (output)
	 *@return: list of relations
	 */
	public List<Relation> extractRelation(String input, List<Concept> conceptLstOut, List<Sentence> sentenceLstOut) throws IOException {
		ConceptCore cc = new ConceptCore();
		RelationCore rc = new RelationCore();
		ProcessText pt = ProcessVNText.getInstance();
		sentenceLstOut = pt.processDocument(input, false);
		conceptLstOut = cc.extractConcept(sentenceLstOut);
		List<Relation> relLst = rc.extractRelation(sentenceLstOut, conceptLstOut);
		System.out.println("Concept List: ");
		for (Concept concept : conceptLstOut) {
			System.out.println(concept.toString());
		}
		System.out.println("Relation List: ");
		for (Relation rel : relLst) {
			System.out.println(rel.toString());
		}
		
		return relLst;
	}
}
