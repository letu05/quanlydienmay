package com.mycompany.quanlydienmay.dao;

import java.util.List;

import org.hibernate.Session;

import com.mycompany.quanlydienmay.model.Order;
import com.mycompany.quanlydienmay.utils.HibernateUtils;

public class OrderDAO {

    public List<Order> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT o FROM Order o "
                    + "LEFT JOIN FETCH o.items i "
                    + "LEFT JOIN FETCH i.product "
                    + "WHERE o.user.id > 0 ORDER BY o.id DESC",
                    Order.class).list();
        } catch (Exception e) {
            System.err.println("[OrderDAO] Error in findAll: " + e.getMessage());
            return List.of(); // Return empty list instead of crashing
        }
    }

    public List<Order> findByUserId(int userId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.user.id = :userId",
                    Order.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    public Order findById(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Order.class, id);
        }
    }

    public Order findByIdWithItems(int id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            List<Order> result = session.createQuery(
                    "SELECT DISTINCT o FROM Order o "
                    + "LEFT JOIN FETCH o.items i "
                    + "LEFT JOIN FETCH i.product "
                    + "WHERE o.id = :id",
                    Order.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .list();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    public void save(Order order) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.saveOrUpdate(order);
            tx.commit();
        }
    }
}
