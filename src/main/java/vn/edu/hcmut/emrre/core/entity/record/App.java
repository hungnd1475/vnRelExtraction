package vn.edu.hcmut.emrre.core.entity.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.entity.sentence.SentenceDAO;
import vn.edu.hcmut.emrre.core.entity.sentence.SentenceDAOImpl;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SentenceDAO sentenceDAO = new SentenceDAOImpl();
        Sentence senten = sentenceDAO.findByRecordAndLineIndex("1", 1);
        if (senten != null)
        	System.out.println(senten.getWords().get(0).getPosTag());

        // System.out.println(sentence.getWords());

    }
}