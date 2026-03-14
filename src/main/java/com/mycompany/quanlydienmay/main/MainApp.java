/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlydienmay.main;

/**
 *
 * @author tu650
 */
import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.mycompany.quanlydienmay.model.Account;
import com.mycompany.quanlydienmay.model.Cart;
import com.mycompany.quanlydienmay.model.CartItem;
import com.mycompany.quanlydienmay.model.Category;
import com.mycompany.quanlydienmay.model.Order;
import com.mycompany.quanlydienmay.model.OrderItem;
import com.mycompany.quanlydienmay.model.Product;
import com.mycompany.quanlydienmay.utils.HibernateUtils;

public class MainApp {
    public static void main(String[] args) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction trans = session.beginTransaction();

            Account account = new Account(
                    "Nguyen Van A",
                    "0901234567",
                    "vana@example.com",
                    "nguyenvana",
                    "hashed-password",
                    "CUSTOMER"
            );
            session.save(account);

            Category cat = new Category("Tủ Lạnh");
            session.save(cat);

            Product p1 = new Product(
                    "Samsung Inverter 400L",
                    new BigDecimal("15000000.00"),
                    10,
                    "Tủ lạnh inverter dung tích 400L",
                    "/images/samsung-inverter-400l.jpg",
                    cat
            );
            session.save(p1);

            Cart cart = new Cart();
            cart.setUser(account);
            session.save(cart);

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(p1);
            cartItem.setQuantity(1);
            session.save(cartItem);

            Order order = new Order();
            order.setUser(account);
            order.setCustomerName(account.getFullName());
            order.setCustomerPhone(account.getPhone());
            order.setCustomerAddress("123 Nguyễn Trãi, Hà Nội");
            order.setEmail(account.getEmail());
            order.setNote("Giao giờ hành chính");
            order.setStatus("PENDING");
            order.setPaymentMethod("COD");
            session.save(order);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(p1);
            item.setQuantity(1);
            item.setUnitPrice(p1.getPrice());
            session.save(item);

            trans.commit();
            System.out.println("Đã khởi tạo schema BCNF và dữ liệu mẫu thành công!");
        }
    }
}