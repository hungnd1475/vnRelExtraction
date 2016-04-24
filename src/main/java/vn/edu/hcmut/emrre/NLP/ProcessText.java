package vn.edu.hcmut.emrre.NLP;

import java.util.List;

import vn.edu.hcmut.emrre.entity.Concept;
import vn.edu.hcmut.emrre.entity.Sentence;

public interface ProcessText {
    public String wordSegment(String text);

    public String[] wordsSegment(String text);

    public String posTagging(String text);

    public List<Sentence> processDocument(String content, boolean isSentenceSegmented);

    public List<String> preProcessDoc(String content);

    public List<Sentence> parseToSentences(List<String> lines);

    public List<Concept> parseToConcepts(List<String> concepts);
}
