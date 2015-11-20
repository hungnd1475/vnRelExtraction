package vn.edu.hcmut.emrre.core.preprocess;

import java.util.ArrayList;
import java.util.List;

import jvntextpro.JVnTextPro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.entity.word.Word;

public class ProcessVNText implements ProcessText {

    public static final String modelsPath = "src/main/resources/jvnTextPro/models";
    private static final Logger LOG = LoggerFactory.getLogger(ProcessVNText.class);
    private static ProcessVNText processVNText;
    private JVnTextPro jVnTextPro;

    public static ProcessVNText getInstance() {
        if (processVNText == null) {
            processVNText = new ProcessVNText();
        }
        return processVNText;
    }

    private ProcessVNText() {
        initJVNTextPro();
    }

    private void initJVNTextPro() {
        jVnTextPro = new JVnTextPro();
        jVnTextPro.initSenSegmenter(modelsPath + "\\jvnsensegmenter");
        jVnTextPro.initSenTokenization();
        jVnTextPro.initPosTagger(modelsPath + "\\jvnpostag\\maxent");
        jVnTextPro.initSegmenter(modelsPath + "\\jvnsegmenter");
    }

    @Override
    public String wordSegment(String text) {
        String result = "";
        return result;
    }

    @Override
    public String[] wordsSegment(String text) {

        return null;
    }

    @Override
    public List<Sentence> processDocument(String text, boolean isSentenceSegmented) {
        List<Sentence> sentences = new ArrayList<Sentence>();
        if (!isSentenceSegmented) {
            text = jVnTextPro.senSegment(text);
            text = jVnTextPro.senTokenize(text);
        }
        String[] lines = text.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            Sentence currSentence = new Sentence();
            String lineContent = jVnTextPro.wordSegment(lines[i]);
            currSentence.setContent(lineContent);
            currSentence.setIndex((long) i + 1);
            currSentence.setWords(parseSenToWords(lineContent));
            sentences.add(currSentence);
        }

        return sentences;
    }

    @Override
    public List<String> preProcessDoc(String content) {
        List<String> result = new ArrayList<>();
        try {
            String tmp = content;
            tmp = jVnTextPro.senSegment(tmp);
            tmp = jVnTextPro.senTokenize(tmp);
            tmp = jVnTextPro.wordSegment(tmp);
            String[] sens = tmp.split("\n");
            for (String each : sens){
                result.add(each.trim());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    private List<Word> parseSenToWords(String senContent) {
        List<Word> result = new ArrayList<Word>();
        String[] arrWord = senContent.split("\\s");
        for (int i = 0; i < arrWord.length; i++) {
            Word word = new Word();
            word.setContent(arrWord[i]);
            word.setPosTag(posTagging(arrWord[i]));
            word.setIndex((long) i);
            result.add(word);
        }
        return result;
    }

    @Override
    public String posTagging(String text) {
        String result = "";
        try {
            result = jVnTextPro.posTagging(text).split("/")[1];
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    public static void main(String[] args) {

        String str = "Lý Do :  Ho , khò_khè. "
                + "Bệnh Lý : "
                + "Cháu ho , khò_khè đã 10 ngày_nay , đã được khám và điều_trị viêm mũi họng , phế_quản với Zinnat 7 ngày hiện_tại còn ho , khò_khè - > KHám Tiền Sử Bệnh Viêm phế_quản "
                + " Điều Trị: Kháng_sinh , thuốc ho , men tiêu_hóa , kháng_viêm , giãn phế_quản ";

        String text1 = "Lý Do :  Ho , khò khè. "
                + "Bệnh Lý : "
                + "Cháu ho , khò khè đã 10 ngày nay , đã được khám và điều trị viêm mũi họng , phế quản với Zinnat 7 ngày hiện tại còn ho , khò khè - > KHám Tiền Sử Bệnh Viêm phế quản"
                + ". Điều Trị: Kháng sinh , thuốc ho , men tiêu hóa , kháng viêm , giãn phế quản  . ";

        ProcessText processText = ProcessVNText.getInstance();
        List<Sentence> sens = processText.processDocument(text1, false);
        // System.out.println(sens.get(0).getWords());
    }
}