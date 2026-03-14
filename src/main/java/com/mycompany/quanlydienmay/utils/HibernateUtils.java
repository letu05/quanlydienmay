/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlydienmay.utils;

/**
 *
 * @author tu650
 */
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.mycompany.quanlydienmay.model.Account;
import com.mycompany.quanlydienmay.model.Cart;
import com.mycompany.quanlydienmay.model.CartItem;
import com.mycompany.quanlydienmay.model.Category;
import com.mycompany.quanlydienmay.model.Order;
import com.mycompany.quanlydienmay.model.OrderItem;
import com.mycompany.quanlydienmay.model.Product;

public class HibernateUtils {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Account.class)
                    .addAnnotatedClass(Category.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Cart.class)
                    .addAnnotatedClass(CartItem.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(OrderItem.class)
                    .buildSessionFactory();
        } catch (HibernateException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() { return sessionFactory; }
}