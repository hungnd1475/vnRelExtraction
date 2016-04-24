package vn.edu.hcmut.emrre.main;

import java.io.IOException;
import java.util.List;

import javax.swing.text.html.HTML;

import vn.edu.hcmut.emrre.dao.RecordDAO;
import vn.edu.hcmut.emrre.entity.Concept;
import vn.edu.hcmut.emrre.entity.Record;
import vn.edu.hcmut.emrre.entity.Relation;
import vn.edu.hcmut.emrre.entity.Sentence;
import vn.edu.hcmut.emrre.spring.utils.HTMLHelper;
import de.bwaldvogel.liblinear.InvalidInputDataException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidInputDataException {
//        RelationCore emr = new RelationCore();
//        emr.generateModel();
    	List<Record> records = RecordDAO.Instance.findAll();
    	System.out.println(records.size());

    }
}
