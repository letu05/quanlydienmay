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
import org.hibernate.query.Query;

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

            // Kiểm tra admin account có tồn tại không (làm cho script idempotent)
            Query<Account> adminQuery = session.createQuery(
                    "FROM Account WHERE username = 'admin'", Account.class);
            Account admin = adminQuery.uniqueResult();
            
            if (admin == null) {
                // Tài khoản ADMIN để test đăng nhập
                admin = new Account(
                        "Admin System",
                        "0900000001",
                        "admin@example.com",
                        "admin",
                        "admin123",
                        "ADMIN"
                );
                session.save(admin);
                System.out.println("✓ Tạo tài khoản ADMIN");
            } else {
                System.out.println("✓ Tài khoản ADMIN đã tồn tại");
            }

            // Kiểm tra user account có tồn tại không
            Query<Account> userQuery = session.createQuery(
                    "FROM Account WHERE username = 'user'", Account.class);
            Account account = userQuery.uniqueResult();
            
            if (account == null) {
                // Tài khoản CUSTOMER để test đăng nhập
                account = new Account(
                        "Nguyen Van A",
                        "0901234567",
                        "vana@example.com",
                        "user",
                        "user123",
                        "CUSTOMER"
                );
                session.save(account);
                System.out.println("✓ Tạo tài khoản CUSTOMER");
            } else {
                System.out.println("✓ Tài khoản CUSTOMER đã tồn tại");
            }

            // Kiểm tra category
            Query<Category> catQuery = session.createQuery(
                    "FROM Category WHERE name = 'Tủ Lạnh'", Category.class);
            Category cat = catQuery.uniqueResult();
            
            if (cat == null) {
                cat = new Category("Tủ Lạnh");
                session.save(cat);
                System.out.println("✓ Tạo danh mục Tủ Lạnh");
            } else {
                System.out.println("✓ Danh mục Tủ Lạnh đã tồn tại");
            }

            // Kiểm tra product
            Query<Product> prodQuery = session.createQuery(
                    "FROM Product WHERE name = 'Samsung Inverter 400L'", Product.class);
            Product p1 = prodQuery.uniqueResult();
            
            if (p1 == null) {
                p1 = new Product(
                        "Samsung Inverter 400L",
                        new BigDecimal("15000000.00"),
                        10,
                        "Tủ lạnh inverter dung tích 400L",
                        "/images/samsung-inverter-400l.jpg",
                        cat
                );
                session.save(p1);
                System.out.println("✓ Tạo sản phẩm Samsung Inverter 400L");
            } else {
                System.out.println("✓ Sản phẩm Samsung Inverter 400L đã tồn tại");
            }

            // Kiểm tra cart của user (chỉ tạo nếu chưa tồn tại)
            Query<Cart> cartQuery = session.createQuery(
                    "FROM Cart WHERE user.id = :userId", Cart.class);
            cartQuery.setParameter("userId", account.getId());
            Cart cart = cartQuery.uniqueResult();
            
            if (cart == null) {
                cart = new Cart();
                cart.setUser(account);
                session.save(cart);
                System.out.println("✓ Tạo giỏ hàng");
            } else {
                System.out.println("✓ Giỏ hàng đã tồn tại");
            }

            // Kiểm tra cartitem
            Query<CartItem> cartItemQuery = session.createQuery(
                    "FROM CartItem WHERE cart.id = :cartId AND product.id = :productId", CartItem.class);
            cartItemQuery.setParameter("cartId", cart.getId());
            cartItemQuery.setParameter("productId", p1.getId());
            CartItem cartItem = cartItemQuery.uniqueResult();
            
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(p1);
                cartItem.setQuantity(1);
                session.save(cartItem);
                System.out.println("✓ Thêm sản phẩm vào giỏ");
            } else {
                System.out.println("✓ Sản phẩm trong giỏ đã tồn tại");
            }

            // Kiểm tra order của user (tạo order mới nếu chưa có)
            Query<Order> orderQuery = session.createQuery(
                    "FROM Order WHERE user.id = :userId", Order.class);
            orderQuery.setParameter("userId", account.getId());
            Order order = orderQuery.uniqueResult();
            
            if (order == null) {
                order = new Order();
                order.setUser(account);
                order.setCustomerName(account.getFullName());
                order.setCustomerPhone(account.getPhone());
                order.setCustomerAddress("123 Nguyễn Trãi, Hà Nội");
                order.setEmail(account.getEmail());
                order.setNote("Giao giờ hành chính");
                order.setStatus("PENDING");
                order.setPaymentMethod("COD");
                session.save(order);
                System.out.println("✓ Tạo đơn hàng");
            } else {
                System.out.println("✓ Đơn hàng của khách hàng đã tồn tại");
            }

            // Kiểm tra orderitem
            Query<OrderItem> orderItemQuery = session.createQuery(
                    "FROM OrderItem WHERE order.id = :orderId AND product.id = :productId", OrderItem.class);
            orderItemQuery.setParameter("orderId", order.getId());
            orderItemQuery.setParameter("productId", p1.getId());
            OrderItem item = orderItemQuery.uniqueResult();
            
            if (item == null) {
                item = new OrderItem();
                item.setOrder(order);
                item.setProduct(p1);
                item.setQuantity(1);
                item.setUnitPrice(p1.getPrice());
                session.save(item);
                System.out.println("✓ Thêm sản phẩm vào đơn hàng");
            } else {
                System.out.println("✓ Sản phẩm trong đơn hàng đã tồn tại");
            }

            trans.commit();
            System.out.println("\n✓✓✓ Đã khởi tạo schema BCNF và dữ liệu mẫu thành công! ✓✓✓");
            System.out.println("\nTài khoản TEST:");
            System.out.println("  ADMIN:    username='admin',    password='admin123'");
            System.out.println("  CUSTOMER: username='user',     password='user123'");
        } catch (Exception e) {
            System.err.println("❌ LỖI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
