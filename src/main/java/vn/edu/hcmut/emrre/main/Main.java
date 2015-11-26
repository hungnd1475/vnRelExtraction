package vn.edu.hcmut.emrre.main;

import java.io.IOException;
import java.util.List;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;

public class Main {
	public static void main(String[] args) throws IOException, InvalidInputDataException {
		RelationCore emr = new RelationCore();
		//emr.extractRel("Tôi bị ho, đi điều trị ho nhưng không hết bệnh");
		//emr.extractRel("Tôi bị ho, đi điều trị ho và đã hết bệnh");
		List<Concept> conceptLst = null;
		List<Sentence> sentenceLst = null;
		emr.extractRelation("Tôi bị ho, đau đầu", conceptLst, sentenceLst);
	}
}
