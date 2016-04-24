package vn.edu.hcmut.emrre.core.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jvntextpro.JVnTextPro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.hcmut.emrre.core.entity.Concept;
import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;
import vn.edu.hcmut.emrre.core.entity.word.Word;

public class ProcessVNText implements ProcessText {

    public static final String modelsPath = "src/main/resources/jvnTextPro/models";
    private static final Logger LOG = LoggerFactory.getLogger(ProcessVNText.class);
    private static ProcessVNText processVNText;
    private Pattern conceptPattern = Pattern.compile("^c=\"(.*)?\"\\s(\\d+):(\\d+)\\s(\\d+):(\\d+)\\|\\|t=\"(.*)?\"$");
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
        return null;
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
    public List<Sentence> parseToSentences(List<String> lines) {
        List<Sentence> sentences = new ArrayList<Sentence>();
        try {
            for (int i = 0; i < lines.size(); i++) {
                Sentence currSentence = new Sentence();
                String lineContent = lines.get(i);
                currSentence.setContent(lineContent);
                currSentence.setIndex((long) i + 1);
                currSentence.setWords(parseSenToWords(lineContent));
                sentences.add(currSentence);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return sentences;
    }

    @Override
    public List<Concept> parseToConcepts(List<String> concepts) {
        List<Concept> cons = new ArrayList<Concept>();
        Matcher matcher = null;
        int keyStart = 0;
        try {
            String conceptContent = "";
            int lineIndex = 0;
            int begin = 0;
            int end = 0;
            String type = "";
            for (String lineContent : concepts) {
                matcher = conceptPattern.matcher(lineContent);
                if (matcher.find()) {
                    conceptContent = matcher.group(1).trim();
                    lineIndex = Integer.parseInt(matcher.group(2).trim());
                    begin = Integer.parseInt(matcher.group(3).trim());
                    end = Integer.parseInt(matcher.group(5).trim());
                    type = matcher.group(6).trim();
                }

                Concept concept = new Concept("", conceptContent, lineIndex, begin, end, Concept.Type.valueOf(type
                        .toUpperCase()), keyStart++);
                cons.add(concept);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return cons;
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
            for (String each : sens) {
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
        List<String> cons = new ArrayList<String>();
        cons.add("c=\"Ho\" 2:1 2:1||t=\"problem\"");
        ProcessText processText = ProcessVNText.getInstance();
        processText.parseToConcepts(cons);

        // String str = "Lý Do :  Ho , khò_khè. "
        // + "Bệnh Lý : "
        // +
        // "Cháu ho , khò_khè đã 10 ngày_nay , đã được khám và điều_trị viêm mũi họng , phế_quản với Zinnat 7 ngày hiện_tại còn ho , khò_khè - > KHám Tiền Sử Bệnh Viêm phế_quản "
        // +
        // " Điều Trị: Kháng_sinh , thuốc ho , men tiêu_hóa , kháng_viêm , giãn phế_quản ";
        //
        // String text1 = "Lý Do :  Ho , khò khè. "
        // + "Bệnh Lý : "
        // +
        // "Cháu ho , khò khè đã 10 ngày nay , đã được khám và điều trị viêm mũi họng , phế quản với Zinnat 7 ngày hiện tại còn ho , khò khè - > KHám Tiền Sử Bệnh Viêm phế quản"
        // +
        // ". Điều Trị: Kháng sinh , thuốc ho , men tiêu hóa , kháng viêm , giãn phế quản  . ";
        //
        // ProcessText processText = ProcessVNText.getInstance();
        // List<Sentence> sens = processText.processDocument(text1, false);
        // System.out.println(sens.get(1).getContent());
    }
}