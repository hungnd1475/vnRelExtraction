package vn.edu.hcmut.emrre.core.preprocess;

import jvntextpro.JVnTextPro;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;
import vn.hus.nlp.tokenizer.VietTokenizer;

public class ProcessVNText {

    public static final String modelsPath = "src/main/resources/jvnTextPro/models";

    public static void main(String[] args) {
        JVnTextPro jVnTextPro = new JVnTextPro();
        jVnTextPro.initSenSegmenter(modelsPath + "/jvnsensegmenter");
        jVnTextPro.initSenTokenization();
        jVnTextPro.initPosTagger(modelsPath + "/jvnpostag/maxent");
        jVnTextPro.initSegmenter(modelsPath + "/jvnsegmenter");
        // VietTokenizer tokenizer = new VietTokenizer();
        // VietnameseMaxentTagger maxentTagger = new VietnameseMaxentTagger();
        String str = "Lý Do :  Ho , khò_khè ."
                + "Bệnh Lý : "
                + "Cháu ho , khò_khè đã 10 ngày_nay , đã được khám và điều_trị viêm mũi họng , phế_quản với Zinnat 7 ngày hiện_tại còn ho , khò_khè - > KHám Tiền Sử Bệnh Viêm phế_quản "
                + "Điều Trị: Kháng_sinh , thuốc ho , men tiêu_hóa , kháng_viêm , giãn phế_quản ";

        String[] sentences  = jVnTextPro.senTokenize(str).split("\\n");
        for (String string : sentences) {
            System.out.println(string);
        }
        String result = jVnTextPro.wordSegment(str);
        System.out.println(result);

        result = jVnTextPro.posTagging(str);
        System.out.println(result);

//        String rs = jVnTextPro.process(str);
//        System.out.println(rs);
    }
}