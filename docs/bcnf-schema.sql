CREATE DATABASE IF NOT EXISTS ElectronicsStoreDB;
USE ElectronicsStoreDB;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_category_name UNIQUE (category_name)
);

CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    CONSTRAINT uk_account_phone UNIQUE (phone),
    CONSTRAINT uk_account_email UNIQUE (email),
    CONSTRAINT uk_account_username UNIQUE (username)
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    stock INT NOT NULL,
    description VARCHAR(1000),
    image_path VARCHAR(255),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cart_user UNIQUE (user_id),
    CONSTRAINT fk_cart_account FOREIGN KEY (user_id) REFERENCES accounts(id)
);

CREATE TABLE cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_address VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL,
    note VARCHAR(1000),
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    CONSTRAINT fk_order_account FOREIGN KEY (user_id) REFERENCES accounts(id)
);

CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    CONSTRAINT uk_order_product UNIQUE (order_id, product_id),
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES products(id)
);