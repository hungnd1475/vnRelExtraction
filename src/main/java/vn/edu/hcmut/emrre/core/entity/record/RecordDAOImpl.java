package vn.edu.hcmut.emrre.core.entity.record;


import java.util.List;

import org.hibernate.Session;

import vn.edu.hcmut.emrre.common.HibernateUtil;

public class RecordDAOImpl implements RecordDAO {
    
    public void save(Record record) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(record);
        session.getTransaction().commit();
    }

    public void update(Record record) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(record);
        session.getTransaction().commit();
    }

    public Record findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Record record = (Record) session.get(Record.class, id);
        return record;
    }

    public Record findByName(String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "from Record r where r.name = :name";
        Record record = (Record) session.createQuery(hql).setString("name", name).uniqueResult();
        return record;
    }

    public void delete(Record record) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(record);
        session.getTransaction().commit();
    }

    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Record record = (Record) session.get(Record.class, id);
        delete(record);
    }

    @SuppressWarnings("unchecked")
    public List<Record> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Record> records = (List<Record>) session.createQuery("from Record").list();
        return records;
    }

}
