package com.mycompany.quanlydienmay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_details")
public class ProductDetail {
    
    @Id
    private int id; // Same id as Product
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;
    
    @Column(name = "origin", length = 100)
    private String origin;
    
    @Column(name = "warranty_period", length = 50)
    private String warrantyPeriod;
    
    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    public ProductDetail() {}

    public ProductDetail(Product product, String manufacturer, String origin, String warrantyPeriod, String specifications) {
        this.product = product;
        this.manufacturer = manufacturer;
        this.origin = origin;
        this.warrantyPeriod = warrantyPeriod;
        this.specifications = specifications;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getWarrantyPeriod() { return warrantyPeriod; }
    public void setWarrantyPeriod(String warrantyPeriod) { this.warrantyPeriod = warrantyPeriod; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }
}
