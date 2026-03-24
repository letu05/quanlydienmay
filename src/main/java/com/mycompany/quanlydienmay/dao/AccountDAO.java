package com.mycompany.quanlydienmay.dao;

import java.util.List;

import org.hibernate.Session;

import com.mycompany.quanlydienmay.model.Account;
import com.mycompany.quanlydienmay.utils.HibernateUtils;

public class AccountDAO {

    /**
     * Kiểm tra đăng nhập bằng username và password (plain text hoặc hashed).
     * Trả về Account nếu đúng, null nếu sai.
     */
    public Account findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            List<Account> result = session.createQuery(
                    "FROM Account a WHERE a.username = :username AND a.password = :password",
                    Account.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .setMaxResults(1)
                    .list();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    public Account findByUsername(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            List<Account> result = session.createQuery(
                    "FROM Account a WHERE a.username = :username", Account.class)
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .list();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    public List<Account> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("FROM Account", Account.class).list();
        }
    }

    public Account findById(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Account.class, id);
        }
    }

    public void save(Account account) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.saveOrUpdate(account);
            tx.commit();
        }
    }

    public void delete(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            Account account = session.get(Account.class, id);
            if (account != null) {
                session.delete(account);
            }
            tx.commit();
        }
    }
}
