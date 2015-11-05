package vn.edu.hcmut.emrre.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.DocLine;
import vn.edu.hcmut.emrre.core.entity.Relation;
import vn.edu.hcmut.emrre.core.feature.FeatureVn;
import vn.edu.hcmut.emrre.core.svm.SVM;
import vn.edu.hcmut.emrre.core.utils.Constant;
import vn.edu.hcmut.emrre.core.utils.Dictionary;
import vn.edu.hcmut.emrre.core.utils.ReadFile;
import vn.edu.hcmut.emrre.core.utils.WordHandle;
import vn.edu.hcmut.emrre.core.utils.WriteFile;

public class EMRCore {
	private static List<Relation> relations;
	private static List<Concept> concepts;
	private static List<DocLine> doclines;
	private FeatureVn featureExtractor;

	public EMRCore() {
		featureExtractor = new FeatureVn();
	}

	public static List<Relation> getRelations() {
		return relations;
	}

	public static void setRelations(List<Relation> relations) {
		EMRCore.relations = relations;
	}

	public static List<DocLine> getDoclines() {
		return doclines;
	}

	public static List<Concept> getConcepts() {
		return concepts;
	}

	public static void setConcepts(List<Concept> concepts) {
		EMRCore.concepts = concepts;
	}

	public void getConceptData() {
		if (EMRCore.concepts == null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/concept");
			EMRCore.concepts = read.getAllConcept(0);
		}
	}

	public void getRelationData() {
		if (EMRCore.relations == null && EMRCore.concepts != null) {
			ReadFile read = new ReadFile();
			read.setFolder("vn/rel");
			EMRCore.relations = read.getAllRelation(concepts, true);
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
		SVM svm = new SVM(Constant.MODEL_FILE_PATH);
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
}
