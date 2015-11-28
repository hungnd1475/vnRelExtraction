package vn.edu.hcmut.emrre.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.Relation;
import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;

public class Main {
	public static void main(String[] args) throws IOException, InvalidInputDataException {
		RelationCore emr = new RelationCore();
		//emr.extractRel("Tôi bị ho, đi điều trị ho nhưng không hết bệnh");
		//emr.extractRel("Tôi bị ho, đi điều trị ho và đã hết bệnh");
		String text = "Tôi bị ho, đau đầu." +
		" Tôi uống thuốc, điều trị ho nhưng không khỏi. "
				+ "Tôi bị ho, đau đầu.";
		List<Relation> relLst = emr.extractRelation(text);
		List<Concept> conceptLst = emr.getConceptLstOut();
		List<Sentence> sentenceLst = emr.getSentenceLstOut();
		System.out.println("Sentence List: ");
		for (Sentence sentence : sentenceLst) {
			System.out.println(sentence.toString());
		}
		System.out.println("Concept List: ");
		for (Concept concept : conceptLst) {
			System.out.println(concept.toString());
		}
		System.out.println("Relation List: ");
		for (Relation rel : relLst) {
			System.out.println(rel.toString());
		}
		
	}
}
