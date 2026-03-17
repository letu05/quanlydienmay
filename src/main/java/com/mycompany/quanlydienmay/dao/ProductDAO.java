package com.mycompany.quanlydienmay.dao;

import java.util.List;

import org.hibernate.Session;

import com.mycompany.quanlydienmay.model.Product;
import com.mycompany.quanlydienmay.utils.HibernateUtils;

public class ProductDAO {

    public List<Product> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product p LEFT JOIN FETCH p.category", Product.class).list();
        }
    }

    public Product findById(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Product.class, id);
        }
    }

    public void save(Product product) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.saveOrUpdate(product);
            tx.commit();
        }
    }

    public void delete(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            Product p = session.get(Product.class, id);
            if (p != null) {
                session.delete(p);
            }
            tx.commit();
        }
    }
}
