package vn.edu.hcmut.emrre.spring.utils;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.DocLine;
import vn.edu.hcmut.emrre.core.entity.Relation;
import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.entity.word.Word;
import vn.edu.hcmut.emrre.core.io.DataReader;

public class HTMLHelper {
    public static final String PROBLEM_TAG_BEGIN = "<span class=\"problem selected\">";
    public static final String PROBLEM_TAG_END = "</span>";

    public static final String TREATMENT_TAG_BEGIN = "<span class=\"treatment selected\">";
    public static final String TREATMENT_TAG_END = "</span>";

    public static final String TEST_TAG_BEGIN = "<span class=\"test selected\">";
    public static final String TEST_TAG_END = "</span>";

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
                        for (int i = start + 1; i < end; i++) {
                            words.get(i).setHtmlContent("");
                        }
                    }
                }
                for (Word word : words) {
                    htmlContent.append(word.getHtmlContent() + " ");
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
        // List<Sentence> sens =
        List<Concept> concepts = dataReader.readConcepts(inputConceptFile, 0);
        // dataReader.readAssertion(concepts, inputAssertionFile);
        List<Relation> relations = dataReader.readRelations(concepts, inputRelationFile);
        for (Concept concept : concepts) {

            System.out.println(generateHTMLConcept(concept));
        }

    }
}
