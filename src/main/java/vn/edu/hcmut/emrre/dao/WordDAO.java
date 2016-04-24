package vn.edu.hcmut.emrre.dao;

import java.util.List;

import org.hibernate.Session;

import vn.edu.hcmut.emrre.common.HibernateUtil;
import vn.edu.hcmut.emrre.entity.Word;

public class WordDAO {
	
	public static final WordDAO Instance = new WordDAO();
	
	private WordDAO() {}

    public void save(Word word) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(word);
        session.getTransaction().commit();
    }

    public void update(Word word) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(word);
        session.getTransaction().commit();
    }

    public Word findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Word word = (Word) session.get(Word.class, id);
        return word;
    }

    public void delete(Word word) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(word);
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public List<Word> findByIdCondition(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Word> words = (List<Word>) session.createQuery("from Word w where w.id >= :id").setLong("id", id).list();
        return words;
    }

    public void delete(Long id) {
        Word word = findById(id);
        delete(word);
    }

    @SuppressWarnings("unchecked")
    public List<Word> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Word> words = session.createQuery("from Word").list();
        return words;
    }

}
