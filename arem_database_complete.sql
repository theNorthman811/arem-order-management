-- ==================== AREM ORDER MANAGEMENT - SCHEMA COMPLET ====================
-- Script de création complète de la base de données
-- Auteur: Système de gestion des commandes AREM
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

-- ==================== TRIGGERS POUR MISE À JOUR AUTOMATIQUE ====================

-- Trigger pour calculer le total des commandes
DELIMITER $$
CREATE TRIGGER calculate_order_total_after_insert
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders 
    SET total_amount = (
        SELECT COALESCE(SUM(quantity * unit_price), 0) 
        FROM order_items 
        WHERE order_id = NEW.order_id
    )
    WHERE id = NEW.order_id;
END$$

CREATE TRIGGER calculate_order_total_after_update
AFTER UPDATE ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders 
    SET total_amount = (
        SELECT COALESCE(SUM(quantity * unit_price), 0) 
        FROM order_items 
        WHERE order_id = NEW.order_id
    )
    WHERE id = NEW.order_id;
END$$

CREATE TRIGGER calculate_order_total_after_delete
AFTER DELETE ON order_items
FOR EACH ROW
BEGIN
    UPDATE orders 
    SET total_amount = (
        SELECT COALESCE(SUM(quantity * unit_price), 0) 
        FROM order_items 
        WHERE order_id = OLD.order_id
    )
    WHERE id = OLD.order_id;
END$$

-- Trigger pour calculer le total des commandes fournisseurs
CREATE TRIGGER calculate_supplier_order_total_after_insert
AFTER INSERT ON supplier_order_items
FOR EACH ROW
BEGIN
    UPDATE supplier_orders 
    SET total_amount = (
        SELECT COALESCE(SUM(quantity_ordered * unit_price), 0) 
        FROM supplier_order_items 
        WHERE supplier_order_id = NEW.supplier_order_id
    )
    WHERE id = NEW.supplier_order_id;
END$$

CREATE TRIGGER calculate_supplier_order_total_after_update
AFTER UPDATE ON supplier_order_items
FOR EACH ROW
BEGIN
    UPDATE supplier_orders 
    SET total_amount = (
        SELECT COALESCE(SUM(quantity_ordered * unit_price), 0) 
        FROM supplier_order_items 
        WHERE supplier_order_id = NEW.supplier_order_id
    )
    WHERE id = NEW.supplier_order_id;
END$$

CREATE TRIGGER calculate_supplier_order_total_after_delete
AFTER DELETE ON supplier_order_items
FOR EACH ROW
BEGIN
    UPDATE supplier_orders 
    SET total_amount = (
        SELECT COALESCE(SUM(quantity_ordered * unit_price), 0) 
        FROM supplier_order_items 
        WHERE supplier_order_id = OLD.supplier_order_id
    )
    WHERE id = OLD.supplier_order_id;
END$$

-- Trigger pour mettre à jour les statistiques client
CREATE TRIGGER update_customer_stats_after_order
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.status IN ('DELIVERED', 'COMPLETED', 'PAID') AND OLD.status NOT IN ('DELIVERED', 'COMPLETED', 'PAID') THEN
        UPDATE customers 
        SET 
            total_spent = (
                SELECT COALESCE(SUM(total_amount), 0) 
                FROM orders 
                WHERE customer_id = NEW.customer_id 
                AND status IN ('DELIVERED', 'COMPLETED', 'PAID')
            ),
            orders_count = (
                SELECT COUNT(*) 
                FROM orders 
                WHERE customer_id = NEW.customer_id 
                AND status IN ('DELIVERED', 'COMPLETED', 'PAID')
            ),
            last_order_date = NEW.order_date,
            is_vip = (
                SELECT CASE 
                    WHEN COALESCE(SUM(total_amount), 0) > 1000 THEN TRUE 
                    ELSE FALSE 
                END
                FROM orders 
                WHERE customer_id = NEW.customer_id 
                AND status IN ('DELIVERED', 'COMPLETED', 'PAID')
            )
        WHERE id = NEW.customer_id;
    END IF;
END$$

DELIMITER ;

-- ==================== DONNÉES INITIALES ====================

-- Insertion de l'administrateur principal
INSERT INTO sellers (first_name, last_name, email, password, phone_number, address, is_admin, is_active) VALUES
('Admin', 'Principal', 'admin@admin.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+33123456789', '123 Rue Admin, 75001 Paris', TRUE, TRUE);

-- Insertion d'un client de test
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, is_active) VALUES
('Client', 'Test', 'essaie@client.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+33987654321', '456 Rue Client, 75002 Paris', TRUE);

-- Insertion de fournisseurs de test
INSERT INTO providers (name, email, phone, address, contact_person, is_active) VALUES
('TechSupply Inc.', 'contact@techsupply.com', '+33145678901', '789 Avenue Tech, 75003 Paris', 'Jean Dupont', TRUE),
('Global Components', 'sales@globalcomp.com', '+33145678902', '321 Boulevard Global, 75004 Paris', 'Marie Martin', TRUE),
('Quality Materials', 'info@qualitymat.com', '+33145678903', '654 Rue Quality, 75005 Paris', 'Pierre Durand', TRUE);

-- Insertion de produits de test
INSERT INTO products (name, reference, description, category, marque, measure, quantity, min_stock, price, cost_price, provider_id, is_active, created_by) VALUES
('Ordinateur Portable HP', 'HP-LAP-001', 'Ordinateur portable HP 15 pouces, 8GB RAM, 256GB SSD', 'Informatique', 'HP', 'Piece', 15.000, 5.000, 799.99, 599.99, 1, TRUE, 1),
('Souris Logitech', 'LOG-MOU-001', 'Souris optique Logitech sans fil', 'Informatique', 'Logitech', 'Piece', 50.000, 10.000, 29.99, 19.99, 1, TRUE, 1),
('Clavier Mécanique', 'KEY-MEC-001', 'Clavier mécanique RGB rétroéclairé', 'Informatique', 'Corsair', 'Piece', 25.000, 5.000, 129.99, 89.99, 2, TRUE, 1),
('Écran 24 pouces', 'MON-24-001', 'Écran LED 24 pouces Full HD', 'Informatique', 'Samsung', 'Piece', 12.000, 3.000, 199.99, 149.99, 2, TRUE, 1),
('Câble USB-C', 'CAB-USC-001', 'Câble USB-C 2 mètres', 'Accessoires', 'Anker', 'Piece', 100.000, 20.000, 19.99, 9.99, 3, TRUE, 1);

-- Insertion des prix actuels
INSERT INTO prices (product_id, price_type, amount, is_current, created_by) VALUES
(1, 'Sell', 799.99, TRUE, 1),
(1, 'Buy', 599.99, TRUE, 1),
(2, 'Sell', 29.99, TRUE, 1),
(2, 'Buy', 19.99, TRUE, 1),
(3, 'Sell', 129.99, TRUE, 1),
(3, 'Buy', 89.99, TRUE, 1),
(4, 'Sell', 199.99, TRUE, 1),
(4, 'Buy', 149.99, TRUE, 1),
(5, 'Sell', 19.99, TRUE, 1),
(5, 'Buy', 9.99, TRUE, 1);

-- Insertion d'un panier actif pour le client test
INSERT INTO carts (customer_id, status) VALUES (1, 'ACTIVE');

-- Insertion d'articles dans le panier
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price) VALUES
(1, 2, 2.000, 29.99),
(1, 5, 1.000, 19.99);

-- Insertion de commandes de test
INSERT INTO orders (customer_id, status, order_date, total_amount, delivery_address) VALUES
(1, 'DELIVERED', '2025-01-15 10:30:00', 179.97, '456 Rue Client, 75002 Paris'),
(1, 'PENDING', '2025-01-28 14:20:00', 59.98, '456 Rue Client, 75002 Paris');

-- Insertion d'articles de commande
INSERT INTO order_items (order_id, product_id, quantity, unit_price, measure) VALUES
(1, 1, 1.000, 799.99, 'Piece'),
(1, 2, 1.000, 29.99, 'Piece'),
(2, 3, 1.000, 129.99, 'Piece'),
(2, 5, 2.000, 19.99, 'Piece');

-- Insertion de commandes fournisseurs
INSERT INTO supplier_orders (provider_id, order_number, status, order_date, expected_delivery_date, total_amount, created_by) VALUES
(1, 'SUP-2025-001', 'RECEIVED', '2025-01-10 09:00:00', '2025-01-17 09:00:00', 2999.95, 1),
(2, 'SUP-2025-002', 'PENDING', '2025-01-25 11:00:00', '2025-02-01 11:00:00', 1799.94, 1);

-- Insertion d'articles de commande fournisseur
INSERT INTO supplier_order_items (supplier_order_id, product_id, quantity_ordered, quantity_received, unit_price) VALUES
(1, 1, 5.000, 5.000, 599.99),
(1, 2, 20.000, 20.000, 19.99),
(2, 3, 10.000, 0.000, 89.99),
(2, 4, 8.000, 0.000, 149.99);

-- Insertion d'une facture de test (générée automatiquement pour la commande livrée)
INSERT INTO factures (numero_facture, order_id, customer_id, date_emission, date_echeance, montant_ht, taux_tva, montant_tva, montant_ttc, statut, notes, created_by) VALUES
('FACT-2025-001', 1, 1, '2025-01-15 10:30:00', '2025-02-14 10:30:00', 149.98, 20.00, 29.99, 179.97, 'PAYEE', 'Facture générée automatiquement pour la commande #1', 1);

-- ==================== VUES UTILES ====================

-- Vue des produits avec leurs stocks et prix
CREATE OR REPLACE VIEW v_products_summary AS
SELECT 
    p.id,
    p.name,
    p.reference,
    p.category,
    p.marque,
    p.quantity,
    p.min_stock,
    p.price,
    p.cost_price,
    (p.price - p.cost_price) as margin,
    CASE 
        WHEN p.quantity <= 0 THEN 'RUPTURE'
        WHEN p.quantity <= p.min_stock THEN 'FAIBLE'
        ELSE 'OK'
    END as stock_status,
    pr.name as provider_name,
    p.is_active
FROM products p
LEFT JOIN providers pr ON p.provider_id = pr.id;

-- Vue des commandes avec détails client
CREATE OR REPLACE VIEW v_orders_summary AS
SELECT 
    o.id,
    o.status,
    o.order_date,
    o.total_amount,
    CONCAT(c.first_name, ' ', c.last_name) as customer_name,
    c.email as customer_email,
    c.phone_number as customer_phone,
    COUNT(oi.id) as items_count
FROM orders o
JOIN customers c ON o.customer_id = c.id
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, o.status, o.order_date, o.total_amount, c.first_name, c.last_name, c.email, c.phone_number;

-- Vue des factures avec détails
CREATE OR REPLACE VIEW v_factures_summary AS
SELECT 
    f.id,
    f.numero_facture,
    f.date_emission,
    f.date_echeance,
    f.montant_ttc,
    f.statut,
    f.email_envoye,
    CONCAT(c.first_name, ' ', c.last_name) as customer_name,
    c.email as customer_email,
    o.id as order_id,
    CASE 
        WHEN f.statut != 'PAYEE' AND f.date_echeance < NOW() THEN TRUE
        ELSE FALSE
    END as en_retard,
    CASE 
        WHEN f.statut != 'PAYEE' AND f.date_echeance < NOW() THEN DATEDIFF(NOW(), f.date_echeance)
        ELSE 0
    END as jours_retard
FROM factures f
JOIN customers c ON f.customer_id = c.id
JOIN orders o ON f.order_id = o.id;

-- ==================== INDEX ADDITIONNELS POUR PERFORMANCE ====================

CREATE INDEX idx_products_stock_status ON products(quantity, min_stock);
CREATE INDEX idx_orders_date_status ON orders(order_date, status);
CREATE INDEX idx_factures_echeance_statut ON factures(date_echeance, statut);
CREATE INDEX idx_customers_stats ON customers(total_spent, orders_count, is_vip);

-- ==================== PROCÉDURES STOCKÉES UTILES ====================

DELIMITER $$

-- Procédure pour nettoyer les anciens paniers abandonnés
CREATE PROCEDURE CleanAbandonedCarts()
BEGIN
    -- Marquer comme abandonnés les paniers inactifs depuis plus de 30 jours
    UPDATE carts 
    SET status = 'ABANDONED' 
    WHERE status = 'ACTIVE' 
    AND modification_date < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- Supprimer les paniers abandonnés depuis plus de 90 jours
    DELETE FROM carts 
    WHERE status = 'ABANDONED' 
    AND modification_date < DATE_SUB(NOW(), INTERVAL 90 DAY);
END$$

-- Procédure pour générer des statistiques de vente
CREATE PROCEDURE GetSalesStatistics(IN start_date DATE, IN end_date DATE)
BEGIN
    SELECT 
        DATE(o.order_date) as date_vente,
        COUNT(o.id) as nombre_commandes,
        SUM(o.total_amount) as chiffre_affaires,
        AVG(o.total_amount) as panier_moyen,
        COUNT(DISTINCT o.customer_id) as clients_uniques
    FROM orders o
    WHERE o.status IN ('DELIVERED', 'COMPLETED', 'PAID')
    AND DATE(o.order_date) BETWEEN start_date AND end_date
    GROUP BY DATE(o.order_date)
    ORDER BY date_vente DESC;
END$$

-- Procédure pour obtenir les produits à réapprovisionner
CREATE PROCEDURE GetProductsToRestock()
BEGIN
    SELECT 
        p.id,
        p.name,
        p.reference,
        p.quantity as stock_actuel,
        p.min_stock as seuil_minimum,
        (p.min_stock * 2 - p.quantity) as quantite_suggeree,
        pr.name as fournisseur,
        pr.email as email_fournisseur
    FROM products p
    LEFT JOIN providers pr ON p.provider_id = pr.id
    WHERE p.is_active = TRUE
    AND p.quantity <= p.min_stock
    ORDER BY (p.quantity / p.min_stock) ASC;
END$$

DELIMITER ;

-- ==================== ÉVÉNEMENTS AUTOMATIQUES ====================

-- Événement pour nettoyer automatiquement les paniers abandonnés (chaque jour à 2h du matin)
CREATE EVENT IF NOT EXISTS evt_clean_abandoned_carts
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURRENT_DATE + INTERVAL 1 DAY, '02:00:00')
DO
  CALL CleanAbandonedCarts();

-- ==================== INFORMATIONS FINALES ====================

SELECT 'SCHEMA AREM CRÉÉ AVEC SUCCÈS!' as MESSAGE;
SELECT 
    'TABLES' as TYPE,
    COUNT(*) as NOMBRE
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_type = 'BASE TABLE'

UNION ALL

SELECT 
    'VUES' as TYPE,
    COUNT(*) as NOMBRE
FROM information_schema.views 
WHERE table_schema = DATABASE()

UNION ALL

SELECT 
    'TRIGGERS' as TYPE,
    COUNT(*) as NOMBRE
FROM information_schema.triggers 
WHERE trigger_schema = DATABASE()

UNION ALL

SELECT 
    'PROCÉDURES' as TYPE,
    COUNT(*) as NOMBRE
FROM information_schema.routines 
WHERE routine_schema = DATABASE() 
AND routine_type = 'PROCEDURE';

-- ==================== FIN DU SCRIPT ==================== 