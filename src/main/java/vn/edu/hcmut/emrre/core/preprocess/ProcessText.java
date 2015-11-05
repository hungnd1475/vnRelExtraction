package vn.edu.hcmut.emrre.core.preprocess;

public interface ProcessText {
    public String wordSegment(String text);

    public String[] wordsSegment(String text);

    public String posTagging(String text);

}
