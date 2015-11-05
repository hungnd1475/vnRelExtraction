package vn.edu.hcmut.emrre.core.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.DocLine;
import vn.edu.hcmut.emrre.core.entity.Relation;
import vn.edu.hcmut.emrre.core.entity.record.Record;
import vn.edu.hcmut.emrre.core.entity.record.RecordDAO;
import vn.edu.hcmut.emrre.core.entity.record.RecordDAOImpl;

public class DataReader {
    private Pattern conceptPattern = Pattern.compile("^c=\"(.*)?\"\\s(\\d+):(\\d+)\\s(\\d+):(\\d+)\\|\\|t=\"(.*)?\"$");
    private Pattern assertion = Pattern.compile("^c=\"(.*)?\"\\s(\\d+):(\\d+)\\s(\\d+):(\\d+)\\|\\|t=\"(.*)?\"\\|\\|a=\"(.*)?\"$");
    private Pattern relationPattern = Pattern
            .compile("c=\"(.*)?\"\\s(\\d+):(\\d+)\\s(\\d+):(\\d+).*r=\"(.*)?\".*c=\"(.*)?\"\\s(\\d+):(\\d+)\\s(\\d+):(\\d+)$");

    public List<DocLine> readDocument(String inputFile) {
        List<DocLine> docLines = new ArrayList<DocLine>();
        try {
            // Get file from resources folder
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStreamReader is = new InputStreamReader(classLoader.getResourceAsStream(inputFile));
            BufferedReader br = new BufferedReader(is);

            String fileName = getFileNameFromPath(inputFile).split("\\.")[0];
            String content = "";
            int lineIndex = 1;

            while ((content = br.readLine()) != null) {
                DocLine docLine = new DocLine(fileName, content, lineIndex);
                docLines.add(docLine);
                lineIndex++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docLines;
    }

    public List<Concept> readConcepts(String inputFile, int keyStart) {
        List<Concept> concepts = new ArrayList<Concept>();
        Matcher matcher = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStreamReader is = new InputStreamReader(classLoader.getResourceAsStream(inputFile));
            BufferedReader br = new BufferedReader(is);

            String fileName = getFileNameFromPath(inputFile).split("\\.")[0];
            String lineContent = "";

            while ((lineContent = br.readLine()) != null) {
                String conceptContent = "";
                int lineIndex = 0;
                int begin = 0;
                int end = 0;
                String type = "";

                matcher = conceptPattern.matcher(lineContent);
                if (matcher.find()) {
                    conceptContent = matcher.group(1).trim();
                    lineIndex = Integer.parseInt(matcher.group(2).trim());
                    begin = Integer.parseInt(matcher.group(3).trim());
                    end = Integer.parseInt(matcher.group(5).trim());
                    type = matcher.group(6).trim();
                }

                Concept concept = new Concept(fileName, conceptContent, lineIndex, begin, end,
                        Concept.Type.valueOf(type.toUpperCase()), keyStart++);
                // System.err.println(concept);
                concepts.add(concept);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return concepts;
    }

    public List<Relation> readRelations(List<Concept> concepts, String inputFile) {
        List<Relation> relations = new ArrayList<Relation>();
        Matcher matcher = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStreamReader is = new InputStreamReader(classLoader.getResourceAsStream(inputFile));
            BufferedReader br = new BufferedReader(is);

            String fileName = getFileNameFromPath(inputFile).split("\\.")[0];
            
            String lineContent = "";

            while ((lineContent = br.readLine()) != null) {
                String conceptContent1 = "";
                int lineIndex1 = 0;
                int begin1 = 0;
                int end1 = 0;

                String conceptContent2 = "";
                int lineIndex2 = 0;
                int begin2 = 0;
                int end2 = 0;

                String relationType = "";

                matcher = relationPattern.matcher(lineContent);
                if (matcher.find()) {
                    conceptContent1 = matcher.group(1).trim();
                    lineIndex1 = Integer.parseInt(matcher.group(2).trim());
                    begin1 = Integer.parseInt(matcher.group(3).trim());
                    end1 = Integer.parseInt(matcher.group(5).trim());

                    conceptContent2 = matcher.group(7).trim();
                    lineIndex2 = Integer.parseInt(matcher.group(8).trim());
                    begin2 = Integer.parseInt(matcher.group(9).trim());
                    end2 = Integer.parseInt(matcher.group(11).trim());

                    relationType = matcher.group(6).trim();
                }

                Concept concept1 = findConcept(concepts, conceptContent1, lineIndex1, begin1, end1, fileName);
                Concept concept2 = findConcept(concepts, conceptContent2, lineIndex2, begin2, end2, fileName);
                if (concept1 != null && concept2 != null) {
                    Relation relation = new Relation(fileName, concept1, concept2,
                            Relation.Type.valueOf(relationType), relations.size());
                    // System.out.println(relation);
                    relations.add(relation);
                    // add each concept key to the relateLst of other
                    concept1.addRelateLst(concept2);
                    concept2.addRelateLst(concept1);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relations;
    }

    public void readAssertion(List<Concept> concepts, String inputFile){
        Matcher matcher = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStreamReader is = new InputStreamReader(classLoader.getResourceAsStream(inputFile));
            BufferedReader br = new BufferedReader(is);

            String fileName = getFileNameFromPath(inputFile).split("\\.")[0];
            String lineContent = "";

            while ((lineContent = br.readLine()) != null) {
                String conceptContent = "";
                int lineIndex = 0;
                int begin = 0;
                int end = 0;
                String type = "";
                String assertionStr = "";

                matcher = assertion.matcher(lineContent);
                if (matcher.find()) {
                    conceptContent = matcher.group(1).trim();
                    lineIndex = Integer.parseInt(matcher.group(2).trim());
                    begin = Integer.parseInt(matcher.group(3).trim());
                    end = Integer.parseInt(matcher.group(5).trim());
                    type = matcher.group(6).trim();
                    assertionStr = matcher.group(7).trim();
                }

                Concept concept = findConcept(concepts, conceptContent, lineIndex, begin, end, fileName);
                if (concept != null)
                    concept.setAssertion(assertionStr);
                // System.err.println(concept);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getFileNameFromPath(String filePath) {
        String fileName = "";
        int index = filePath.lastIndexOf("/");
        fileName = filePath.substring(index + 1);
        return fileName;
    }

    private Concept findConcept(List<Concept> concepts, String content, int atLine, int begin, int end, String fileName) {
        Concept concept = null;
        Iterator<Concept> iterator = concepts.iterator();
        while (iterator.hasNext()) {
            concept = iterator.next();
            
            if (concept.getContent().equals(content) && concept.getLine() == atLine && concept.getBegin() == begin
                    && concept.getEnd() == end && concept.getFileName().equals(fileName)) {
                return concept;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String inputDocFile = "vn//txt/1.txt";
        String inputConceptFile = "vn/concept/1.txt";
        String inputRelationFile = "vn/rel/1.txt";
 //       String inputAssertionFile = "i2b2data/train/beth/ast/record-13.ast";
        DataReader dataReader = new DataReader();
        List<DocLine> docLines = dataReader.readDocument(inputDocFile);
        List<Concept> concepts = dataReader.readConcepts(inputConceptFile, 0);
 //       dataReader.readAssertion(concepts, inputAssertionFile);
        List<Relation> relations = dataReader.readRelations(concepts, inputRelationFile);

//        String name = concepts.get(0).getFileName().split("\\.")[0];
//        RecordDAO recordDAO = new RecordDAOImpl();
//        Record record = recordDAO.findByName(name);

        for (Concept each : concepts) {
            System.out.println(each.toString());
            System.out.println(each.getLine());
//            System.err.println(record.getSentences().get(each.getLine() - 1).getWords().get(each.getBegin()));
        }
//        for (DocLine each : docLines) {
//            System.out.println(each.getContent());
// //           System.out.println(each.getLine());
////            System.err.println(record.getSentences().get(each.getLine() - 1).getWords().get(each.getBegin()));
//        }
//        for (Relation each : relations) {
//            System.out.println(each.toString());
////           System.out.println(each.getLine());
////            System.err.println(record.getSentences().get(each.getLine() - 1).getWords().get(each.getBegin()));
//        }

        // System.err.println(arr[0]);
        // dataReader.showData(concepts);
    }

    public void showData(List list) {
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }
}
