package vn.edu.hcmut.emrre.core.entity.record;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;

@Entity
@Table(name = "record")
public class Record {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "record")
    private List<Sentence> sentences;

    public Record() {
    }

    public Record(Long id, String text, String name) {
        this.id = id;
        this.text = text;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {

        return String.format("Id:%s | Name:%s\nText:%s\n", id, name, text);
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

}
