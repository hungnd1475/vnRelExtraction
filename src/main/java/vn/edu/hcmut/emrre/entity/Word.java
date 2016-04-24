package vn.edu.hcmut.emrre.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "word")
public class Word {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "position")
    private long index;

    @ManyToOne
    @JoinColumn(name = "sentence_id", referencedColumnName = "id")
    private Sentence sentence;

    @Column(name = "content")
    private String content;

    @Column(name = "ibo_tag")
    @Enumerated(EnumType.STRING)
    private IBOTag iboTag;

    @Column(name = "pos_tag")
    private String posTag;

    @Column(name = "orth_tag")
    private String orthTag;

    @Column(name = "sessionTag")
    private String sessionTag;

    @Column(name = "umls_tag")
    private int umlsTag;

    @Column(name = "lemma")
    private String lemma;

    private String htmlContent = "";

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public IBOTag getIboTag() {
        return iboTag;
    }

    public void setIboTag(IBOTag iboTag) {
        this.iboTag = iboTag;
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getOrthTag() {
        return orthTag;
    }

    public void setOrthTag(String orthTag) {
        this.orthTag = orthTag;
    }

    public int getUmlsTag() {
        return umlsTag;
    }

    public void setUmlsTag(int umlsTag) {
        this.umlsTag = umlsTag;
    }

    public String getSessionTag() {
        return sessionTag;
    }

    public void setSessionTag(String sessionTag) {
        this.sessionTag = sessionTag;
    }

    public enum IBOTag {
        O, B_TE, I_TE, B_TR, I_TR, B_PR, I_PR
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    @Override
    public String toString() {
        return String.format("index:%d | content:%s| pos:%s", index, content, posTag);
    }
}
