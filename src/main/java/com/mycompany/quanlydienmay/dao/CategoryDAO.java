package com.mycompany.quanlydienmay.dao;

import java.util.List;

import org.hibernate.Session;

import com.mycompany.quanlydienmay.model.Category;
import com.mycompany.quanlydienmay.utils.HibernateUtils;

public class CategoryDAO {

    public List<Category> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Category", Category.class).list();
        }
    }

    public Category findById(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Category.class, id);
        }
    }

    public void save(Category category) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.saveOrUpdate(category);
            tx.commit();
        }
    }

    public void delete(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            Category c = session.get(Category.class, id);
            if (c != null) {
                session.delete(c);
            }
            tx.commit();
        }
    }
}
