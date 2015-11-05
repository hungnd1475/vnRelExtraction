package vn.edu.hcmut.emrre.main;

import java.io.IOException;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.core.entity.Concept;

public class Main {
	public static void main(String[] args) throws IOException, InvalidInputDataException {
		EMRCore emr = new EMRCore();
		emr.crossValidation(2);
		// emr.generateModel();

		// Main run = new Main();
		// run.test();
	}

	private void test() {
		Concept.Type type = Concept.Type.PROBLEM;
		Concept.Type type1 = Concept.Type.PROBLEM;
		System.out.println(type.equals(Concept.Type.PROBLEM));
		System.out.println(type == type1);
	}
}
