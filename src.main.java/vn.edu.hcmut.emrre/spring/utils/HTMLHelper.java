package vn.edu.hcmut.emrre.spring.utils;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.entity.word.Word;
import vn.edu.hcmut.emrre.core.io.DataReader;
import vn.edu.hcmut.emrre.core.preprocess.ProcessText;
import vn.edu.hcmut.emrre.core.preprocess.ProcessVNText;
import vn.edu.hcmut.emrre.modal.entity.Concept;
import vn.edu.hcmut.emrre.modal.entity.DocLine;
import vn.edu.hcmut.emrre.modal.entity.Relation;

public class HTMLHelper {
    public static final String PROBLEM_TAG_BEGIN = "<span class=\"problem selected\">";
    public static final String PROBLEM_TAG_END = "</span>";

    public static final String TREATMENT_TAG_BEGIN = "<span class=\"treatment selected\">";
    public static final String TREATMENT_TAG_END = "</span>";

    public static final String TEST_TAG_BEGIN = "<span class=\"test selected\">";
    public static final String TEST_TAG_END = "</span>";

    public static <T> List<String> generateRawData(List<T> lst) {
        List<String> result = new ArrayList<String>();
        for (T entry : lst) {
            result.add(entry.toString());
        }
        return result;
    }

    public static List<String> generateHTMLSens(List<Sentence> sens, List<Concept> cons) {
        List<String> result = new ArrayList<String>();
        if (sens != null) {
            for (Sentence sen : sens) {
                StringBuilder htmlContent = new StringBuilder();
                long senIndex = sen.getIndex();
                List<Word> words = sen.getWords();
                for (Word word : words) {
                    String tmpHtml = word.getContent() != null ? word.getContent() : "";
                    word.setHtmlContent(tmpHtml);
                }
                for (Concept concept : cons) {
                    if (concept.getLine() == senIndex) {
                        int start = concept.getBegin() - 1;
                        int end = concept.getEnd() - 1;
                        words.get(start).setHtmlContent(generateHTMLConcept(concept));
                        for (int i = start + 1; i <= end; i++) {
                            words.get(i).setHtmlContent("");
                        }
                    }
                }
                for (Word word : words) {
                    if (!word.getHtmlContent().equals("")) {
                        htmlContent.append(word.getHtmlContent() + " ");
                    }
                }
                result.add(htmlContent.toString().trim());
            }
        }
        return result;
    }

    private static String generateHTMLConcept(Concept c) {
        String html = "";
        if (c != null) {
            if (c.getType() == Concept.Type.PROBLEM) {
                html = PROBLEM_TAG_BEGIN + c.getContent() + PROBLEM_TAG_END;
            }

            if (c.getType() == Concept.Type.TREATMENT) {
                html = TREATMENT_TAG_BEGIN + c.getContent() + TREATMENT_TAG_END;
            }

            if (c.getType() == Concept.Type.TEST) {
                html = TEST_TAG_BEGIN + c.getContent() + TEST_TAG_END;
            }
        }
        return html;
    }

    public static void main(String[] args) {
        String inputDocFile = "vn//txt/1.txt";
        String inputConceptFile = "vn/concept/1.txt";
        String inputRelationFile = "vn/rel/1.txt";
        DataReader dataReader = new DataReader();
        List<DocLine> docLines = dataReader.readDocument(inputDocFile);
        String text = "Lý Do "
                + "\nHo , khò khè "
                + "\nBệnh Lý "
                + "\nCháu ho , khò khè đã 10 ngày nay , đã được khám và điều trị viêm mũi họng , phế quản với Zinnat 7 ngày hiện tại còn ho , khò khè -> KHám "
                + "\nTiền Sử Bệnh \nViêm phế quản \nĐiều Trị"
                + "\nKháng sinh , thuốc ho , men tiêu hóa , kháng viêm , giãn phế quản";
        ProcessText processText = ProcessVNText.getInstance();
        List<Sentence> sens = processText.processDocument(text, false);
        List<Concept> concepts = dataReader.readConcepts(inputConceptFile, 0);
        // dataReader.readAssertion(concepts, inputAssertionFile);
        List<Relation> relations = dataReader.readRelations(concepts, inputRelationFile);

        List<String> htmls = generateHTMLSens(sens, concepts);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>");
        for (String string : htmls) {
            System.out.println(string);
        }

    }
}
