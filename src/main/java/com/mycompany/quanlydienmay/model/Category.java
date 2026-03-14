/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlydienmay.model;

/**
 *
 * @author tu650
 */
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
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(name = "uk_category_name", columnNames = "category_name")
})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "category_name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

    public Category() {}
    public Category(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
