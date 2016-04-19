package vn.edu.hcmut.emrre.core.entity.record;

import java.util.List;

public interface RecordDAO {

    public void save(Record record);

    public void update(Record record);

    public Record findById(Long id);
    
    public Record findByName(String name);

    public void delete(Record record);

    public void delete(Long id);

    public List<Record> findAll();

    // public void deleteAll();

}
