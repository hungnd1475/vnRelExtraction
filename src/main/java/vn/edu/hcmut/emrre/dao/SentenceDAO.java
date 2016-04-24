package vn.edu.hcmut.emrre.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import vn.edu.hcmut.emrre.common.HibernateUtil;
import vn.edu.hcmut.emrre.entity.Sentence;
	
public class SentenceDAO {
	
	public static final SentenceDAO Instance = new SentenceDAO();
	
	private SentenceDAO() {}
	
    private Session session;
    
    private void closeSession(){
        session.getTransaction().commit();
        session.close();
    }

    public void save(Sentence sentence) {
        //Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(sentence);
        session.getTransaction().commit();
    }

    public void update(Sentence sentence) {
        //Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(sentence);
        session.getTransaction().commit();
    }

    public Sentence findById(Long id) {
        //Session session = HibernateUtil.getSessionFactory().openSession();
        Sentence sentence = (Sentence) session.get(Sentence.class, id);
        return sentence;
    }

    public Sentence findByRecordAndLineIndex(String recordName, Integer lineIndex) {
        //Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session
                .createQuery("from Sentence s where s.record.name = :recordName and s.index = :lineIndex");
        return (Sentence) query.setString("recordName", recordName).setLong("lineIndex", lineIndex).uniqueResult();
    }

    public void delete(Sentence sentence) {
        //Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(sentence);
        session.getTransaction().commit();
    }

    public void delete(Long id) {
        Sentence sentence = findById(id);
        delete(sentence);
    }

    @SuppressWarnings("unchecked")
    public List<Sentence> findAll() {
        //Session session = HibernateUtil.getSessionFactory().openSession();
        List<Sentence> sentences = session.createQuery("from Sentence").list();
        return sentences;
    }

}
