USE ElectronicsStoreDB;

DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM carts;
DELETE FROM products;
DELETE FROM accounts;
DELETE FROM categories;

INSERT INTO categories (id, category_name) VALUES
    (1, 'Tủ lạnh'),
    (2, 'Máy giặt'),
    (3, 'Điều hòa');

INSERT INTO accounts (id, full_name, phone, email, username, password, role) VALUES
    (1, 'Nguyễn Văn A', '0901234567', 'vana@example.com', 'nguyenvana', 'hashed-password-1', 'CUSTOMER'),
    (2, 'Trần Thị B', '0912345678', 'thib@example.com', 'tranthib', 'hashed-password-2', 'CUSTOMER'),
    (3, 'Lê Quang C', '0923456789', 'quangc@example.com', 'lequangc', 'hashed-password-3', 'ADMIN');

INSERT INTO products (id, name, category_id, price, stock, description, image_path) VALUES
    (1, 'Samsung Inverter 400L', 1, 15000000.00, 10, 'Tủ lạnh inverter dung tích 400L, tiết kiệm điện.', '/images/samsung-inverter-400l.jpg'),
    (2, 'LG TwinWash 12kg', 2, 18500000.00, 5, 'Máy giặt lồng ngang TwinWash 12kg.', '/images/lg-twinwash-12kg.jpg'),
    (3, 'Daikin 1.5 HP Inverter', 3, 12990000.00, 8, 'Điều hòa inverter 1.5 HP làm lạnh nhanh.', '/images/daikin-1-5hp.jpg'),
    (4, 'Panasonic Prime+ 322L', 1, 11200000.00, 6, 'Tủ lạnh Panasonic Prime+ 322L ngăn đông mềm.', '/images/panasonic-prime-322l.jpg');

INSERT INTO carts (id, user_id, created_at) VALUES
    (1, 1, '2026-03-14 08:30:00'),
    (2, 2, '2026-03-14 09:00:00');

INSERT INTO cart_items (id, cart_id, product_id, quantity) VALUES
    (1, 1, 1, 1),
    (2, 1, 3, 1),
    (3, 2, 2, 2);

INSERT INTO orders (id, user_id, customer_name, customer_phone, customer_address, email, note, order_date, status, payment_method) VALUES
    (1, 1, 'Nguyễn Văn A', '0901234567', '123 Nguyễn Trãi, Hà Nội', 'vana@example.com', 'Giao giờ hành chính', '2026-03-14 10:15:00', 'PENDING', 'COD'),
    (2, 2, 'Trần Thị B', '0912345678', '45 Lê Lợi, Đà Nẵng', 'thib@example.com', 'Liên hệ trước khi giao', '2026-03-14 11:00:00', 'CONFIRMED', 'BANK_TRANSFER');

INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES
    (1, 1, 1, 1, 15000000.00),
    (2, 1, 3, 1, 12990000.00),
    (3, 2, 2, 1, 18500000.00),
    (4, 2, 4, 1, 11200000.00);
