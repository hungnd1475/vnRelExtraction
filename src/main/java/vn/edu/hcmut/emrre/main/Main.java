package vn.edu.hcmut.emrre.main;

import java.io.IOException;
import java.util.List;

import javax.swing.text.html.HTML;

import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.modal.entity.Concept;
import vn.edu.hcmut.emrre.modal.entity.Relation;
import vn.edu.hcmut.emrre.spring.utils.HTMLHelper;
import de.bwaldvogel.liblinear.InvalidInputDataException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidInputDataException {
        RelationCore emr = new RelationCore();
        emr.generateModel();
        //emr.crossValidation(5);
        // emr.extractRelation("Tôi bị ho, đi điều trị ho nhưng không hết bệnh");
        // emr.extractRel("Tôi bị ho, đi điều trị ho và đã hết bệnh");
/*        String str = "Lý Do :  Ho , khò khè. "
                + "Bệnh Lý : "
                + "Cháu ho , khò khè đã 10 ngày nay , đã được khám và điều trị viêm mũi họng , phế quản với Zinnat 7 ngày hiện tại còn ho , khò khè - > KHám Tiền Sử Bệnh Viêm phế quản"
                + ". Điều Trị: Kháng sinh , thuốc ho , men tiêu hóa , kháng viêm , giãn phế quản  . ";

        List<Relation> relLst = emr.extractRelation(str);
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

        List<String> htmls = HTMLHelper.generateHTMLSens(sentenceLst, conceptLst);
        for (String string : htmls) {
            System.out.println(string);
        }*/

    }
}
