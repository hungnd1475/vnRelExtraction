package vn.edu.hcmut.emrre.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "sentence")
public class Sentence {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "position")
    private Long index;

    // @Column(name = "record_id")
    private Long recordId;

    @Column(name = "content")
    private String content;

    private Long includePattern;
    private Long isHandle;

    @ManyToOne
    @JoinColumn(name = "record_id", referencedColumnName = "id")
    private Record record;

    @OneToMany(mappedBy = "sentence")
    private List<Word> words;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIndex() {
        return index;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getIncludePattern() {
        return includePattern;
    }

    public void setIncludePattern(Long includePattern) {
        this.includePattern = includePattern;
    }

    public Long getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(Long isHandle) {
        this.isHandle = isHandle;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return String.format("record: %s|index:%d | content: %s", record != null ? record.getName() : "", index,
                content);
    }
}
