package vn.edu.hcmut.emrre.core.entity.sentence;

import java.util.List;

import vn.edu.hcmut.emrre.core.entity.sentence.Sentence;

public interface SentenceDAO {
    public void save(Sentence sentence);

    public void update(Sentence sentence);

    public Sentence findById(Long id);

    public Sentence findByRecordAndLineIndex(String recordName, Integer lineIndex);

    public void delete(Sentence sentence);

    public void delete(Long id);

    public List<Sentence> findAll();
}
