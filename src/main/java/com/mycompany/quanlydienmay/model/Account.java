package com.mycompany.quanlydienmay.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(name = "uk_account_username", columnNames = "username"),
    @UniqueConstraint(name = "uk_account_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_account_phone", columnNames = "phone")
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30)
    private String role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cart> carts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Account() {
    }

    public Account(String fullName, String phone, String email, String username, String password, String role) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Cart> getCarts() { return carts; }
    public void setCarts(List<Cart> carts) { this.carts = carts; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}