-- ==================== AREM ORDER MANAGEMENT - SCHEMA COMPLET ====================
-- Script de création complète de la base de données via Spring Boot
-- Version: 1.1.0
-- Date: 2025-07-30

-- ==================== SUPPRESSION DES TABLES EXISTANTES ====================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS factures;
DROP TABLE IF EXISTS supplier_order_items;
DROP TABLE IF EXISTS supplier_orders;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS prices;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS providers;
DROP TABLE IF EXISTS sellers;

SET FOREIGN_KEY_CHECKS = 1;

-- ==================== CRÉATION DES TABLES ====================

-- Table des vendeurs/administrateurs
CREATE TABLE sellers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    is_admin BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_active (is_active)
);

-- Table des clients
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    address TEXT,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    orders_count INT DEFAULT 0,
    last_order_date TIMESTAMP NULL,
    is_vip BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_phone (phone_number),
    INDEX idx_active (is_active),
    INDEX idx_vip (is_vip)
);

-- Table des fournisseurs
CREATE TABLE providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    contact_person VARCHAR(100),
    payment_terms VARCHAR(100) DEFAULT 'Net 30',
    is_active BOOLEAN DEFAULT TRUE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_active (is_active)
);

-- Table des produits
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    reference VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(100),
    marque VARCHAR(100),
    measure ENUM('Kilogram', 'Gram', 'Liter', 'Milliliter', 'Piece', 'Meter', 'Centimeter') NOT NULL DEFAULT 'Piece',
    quantity DECIMAL(10,3) DEFAULT 0.000,
    min_stock DECIMAL(10,3) DEFAULT 10.000,
    max_stock DECIMAL(10,3) DEFAULT 1000.000,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    cost_price DECIMAL(10,2) DEFAULT 0.00,
    provider_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    
    FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES sellers(id) ON DELETE SET NULL,
    FOREIGN KEY (modified_by) REFERENCES sellers(id) ON DELETE SET NULL,
    
    INDEX idx_name (name),
    INDEX idx_reference (reference),
    INDEX idx_category (category),
    INDEX idx_active (is_active),
    INDEX idx_stock (quantity),
    INDEX idx_provider (provider_id)
);

-- Table des prix (historique des prix)
CREATE TABLE prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    price_type ENUM('Buy', 'Sell') NOT NULL DEFAULT 'Sell',
    amount DECIMAL(10,2) NOT NULL,
    effective_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP NULL,
    is_current BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES sellers(id) ON DELETE SET NULL,
    
    INDEX idx_product (product_id),
    INDEX idx_type (price_type),
    INDEX idx_current (is_current),
    INDEX idx_effective_date (effective_date)
);

-- Table des paniers
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'CONVERTED', 'ABANDONED') DEFAULT 'ACTIVE',
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    
    INDEX idx_customer (customer_id),
    INDEX idx_status (status)
);

-- Table des articles dans le panier
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(10,3) NOT NULL DEFAULT 1.000,
    unit_price DECIMAL(10,2) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_cart_product (cart_id, product_id),
    INDEX idx_cart (cart_id),
    INDEX idx_product (product_id)
);

-- Table des commandes
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status ENUM('NONE', 'PENDING', 'CREATED', 'PARTIALLY_PAID', 'PAID', 'DELIVERED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    delivery_address TEXT,
    delivery_date TIMESTAMP NULL,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    
    INDEX idx_customer (customer_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date)
);

-- Table des articles de commande
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    measure ENUM('Kilogram', 'Gram', 'Liter', 'Milliliter', 'Piece', 'Meter', 'Centimeter') NOT NULL DEFAULT 'Piece',
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
);

-- Table des commandes fournisseurs
CREATE TABLE supplier_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    order_number VARCHAR(100) UNIQUE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'PARTIALLY_RECEIVED', 'RECEIVED', 'CANCELLED') DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_delivery_date TIMESTAMP NULL,
    actual_delivery_date TIMESTAMP NULL,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    created_by BIGINT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES sellers(id) ON DELETE SET NULL,
    
    INDEX idx_provider (provider_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    INDEX idx_order_number (order_number)
);

-- Table des articles de commande fournisseur
CREATE TABLE supplier_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity_ordered DECIMAL(10,3) NOT NULL,
    quantity_received DECIMAL(10,3) DEFAULT 0.000,
    unit_price DECIMAL(10,2) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (supplier_order_id) REFERENCES supplier_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    
    INDEX idx_supplier_order (supplier_order_id),
    INDEX idx_product (product_id)
);

-- Table des factures
CREATE TABLE factures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_facture VARCHAR(20) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    date_emission TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_echeance TIMESTAMP NULL,
    montant_ht DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    taux_tva DECIMAL(5,2) NOT NULL DEFAULT 20.00,
    montant_tva DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    montant_ttc DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    statut ENUM('EMISE', 'ENVOYEE', 'PAYEE', 'ANNULEE', 'AVOIR') DEFAULT 'EMISE',
    type_facture ENUM('VENTE', 'ACHAT', 'AVOIR', 'PROFORMA') DEFAULT 'VENTE',
    notes TEXT,
    conditions_paiement VARCHAR(200) DEFAULT 'Paiement à 30 jours',
    date_paiement TIMESTAMP NULL,
    mode_paiement VARCHAR(100),
    reference_paiement VARCHAR(100),
    fichier_pdf_path VARCHAR(500),
    email_envoye BOOLEAN DEFAULT FALSE,
    date_envoi_email TIMESTAMP NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES sellers(id) ON DELETE SET NULL,
    FOREIGN KEY (modified_by) REFERENCES sellers(id) ON DELETE SET NULL,
    
    UNIQUE KEY uk_order_facture (order_id),
    INDEX idx_numero (numero_facture),
    INDEX idx_customer (customer_id),
    INDEX idx_statut (statut),
    INDEX idx_date_emission (date_emission),
    INDEX idx_date_echeance (date_echeance),
    INDEX idx_email_envoye (email_envoye)
);

-- ==================== INDEX ADDITIONNELS POUR PERFORMANCE ====================

CREATE INDEX idx_products_stock_status ON products(quantity, min_stock);
CREATE INDEX idx_orders_date_status ON orders(order_date, status);
CREATE INDEX idx_factures_echeance_statut ON factures(date_echeance, statut);
CREATE INDEX idx_customers_stats ON customers(total_spent, orders_count, is_vip); 