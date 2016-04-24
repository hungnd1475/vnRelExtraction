package vn.edu.hcmut.emrre.entity;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Relation {
    private String fileName;
    private Concept preConcept;
    private Concept posConcept;
    private Type type;
    private int key;

    public Relation(String fileName, Concept preConcept, Concept posConcept, Type type, int key) {
        if (preConcept.getBegin() < posConcept.getBegin()) {
            this.preConcept = preConcept;
            this.posConcept = posConcept;
        } else {
            this.preConcept = posConcept;
            this.posConcept = preConcept;
        }
        this.type = type;
        this.fileName = fileName;
        this.key = key;
    }

    public Relation(Concept preConcept, Concept posConcept, Type type, int key) {
        this.preConcept = preConcept;
        this.posConcept = posConcept;
        this.type = type;
        this.key = key;
    }

    public static Relation getRelation(int key, List<Relation> relateLst) {
        return relateLst.get(key);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Concept getPreConcept() {
        return preConcept;
    }

    public void setPreConcept(Concept preConcept) {
        this.preConcept = preConcept;
    }

    public Concept getPosConcept() {
        return posConcept;
    }

    public void setPosConcept(Concept posConcept) {
        this.posConcept = posConcept;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getKey() {
        return key;
    }

    public static enum Type {
        TrIP(1), TrAP(2), TrNAP(3), TrCP(4), TrWP(5), TeRP(6), TeCP(7), PIP(8), NONE(0);
        int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static boolean hasRelation(Concept first, Concept second) {
        boolean check = false;
        List<Concept> relateLst1 = first.getRelateLst();
        for (int i = 0; i < relateLst1.size(); i++) {
            if (relateLst1.get(i).getKey() == second.getKey()) {
                check = true;
                break;
            }
        }
        List<Concept> relateLst2 = second.getRelateLst();
        for (int i = 0; i < relateLst2.size(); i++) {
            if (relateLst2.get(i).getKey() == first.getKey()) {
                check = true;
                break;
            }
        }
        return check;
    }

    public static boolean canRelate(Concept first, Concept second) {
        return (first.getType() == Concept.Type.PROBLEM || second.getType() == Concept.Type.PROBLEM)
                & first.getLine() == second.getLine() & first.getFileName() == second.getFileName();
    }

    public static boolean inASentences(int begin1, int begin2, String line) {
        boolean check = false;
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP stan = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(line);
        stan.annotate(annotation);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        int currentPos = 0;
        for (CoreMap sentence : sentences) {
            currentPos += (sentence.get(TokensAnnotation.class)).size();
            boolean check1 = currentPos > begin1;
            boolean check2 = currentPos > begin2;
            if (check1 || check2) {
                check = !(check1 ^ check2);
                break;
            }
        }
        return check;
    }

    public static int valueOfType(Type type) {
        switch (type) {
        case TrIP:
            return 1;
        case TrAP:
            return 2;
        case TrNAP:
            return 3;
        case TrCP:
            return 4;
        case TrWP:
            return 5;
        case TeRP:
            return 6;
        case TeCP:
            return 7;
        case PIP:
            return 8;
        default:
            return 0;
        }
    }

    public static Relation.Type typeOfDouble(int value) {
        switch (value) {
        case 1:
            return Type.TrIP;
        case 2:
            return Type.TrAP;
        case 3:
            return Type.TrNAP;
        case 4:
            return Type.TrCP;
        case 5:
            return Type.TrWP;
        case 6:
            return Type.TeRP;
        case 7:
            return Type.TeCP;
        case 8:
            return Type.PIP;
        default:
            return Type.NONE;
        }
    }

    @Override
    public String toString() {
        return String.format("%s||%s||%s", preConcept.toStringWithoutType(), type, posConcept.toStringWithoutType());
    };
}
