package vn.edu.hcmut.emrre.core.entity.word;

import java.util.List;

public interface WordDAO {
    public void save(Word word);

    public void update(Word word);

    public Word findById(Long id);

    public void delete(Word word);

    public void delete(Long id);

    public List<Word> findAll();
    
    public List<Word> findByIdCondition(Long id);
}
