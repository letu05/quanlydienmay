package com.mycompany.quanlydienmay.utils;

import com.mycompany.quanlydienmay.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CartService - quản lý giỏ hàng session in-memory (không lưu DB).
 * Singleton pattern.
 */
public class CartService {

    public static class CartEntry {
        public final Product product;
        public int quantity;

        public CartEntry(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public BigDecimal lineTotal() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Singleton
    private static CartService instance;

    public static CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    // productId -> CartEntry
    private final Map<Integer, CartEntry> items = new LinkedHashMap<>();

    private CartService() {}

    /** Thêm sản phẩm vào giỏ (nếu đã có thì cộng số lượng) */
    public void addProduct(Product product, int qty) {
        if (qty <= 0) return;
        items.compute(product.getId(), (id, existing) -> {
            if (existing == null) {
                return new CartEntry(product, qty);
            } else {
                existing.quantity += qty;
                return existing;
            }
        });
    }

    /** Xóa 1 sản phẩm khỏi giỏ */
    public void removeProduct(int productId) {
        items.remove(productId);
    }

    /** Cập nhật số lượng; nếu qty <= 0 thì xóa luôn */
    public void updateQuantity(int productId, int qty) {
        if (qty <= 0) {
            items.remove(productId);
        } else {
            CartEntry e = items.get(productId);
            if (e != null) e.quantity = qty;
        }
    }

    /** Lấy danh sách items */
    public List<CartEntry> getItems() {
        return new ArrayList<>(items.values());
    }

    /** Tổng số lượng sản phẩm trong giỏ */
    public int totalQuantity() {
        return items.values().stream().mapToInt(e -> e.quantity).sum();
    }

    /** Tổng tiền */
    public BigDecimal totalAmount() {
        return items.values().stream()
                .map(CartEntry::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Xóa toàn bộ giỏ */
    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
