-- Script pour créer les tables du système de panier
-- À exécuter dans la base de données 'arem'

USE arem;

-- Table cart (panier)
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    creation_date DATETIME NOT NULL,
    modification_date DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

-- Table cart_item (éléments du panier)
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_cart_customer ON cart(customer_id);
CREATE INDEX idx_cart_active ON cart(is_active);
CREATE INDEX idx_cart_item_cart ON cart_item(cart_id);
CREATE INDEX idx_cart_item_product ON cart_item(product_id);

-- Contraintes d'unicité
ALTER TABLE cart_item ADD CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id);

-- Données de test pour le panier (optionnel)
-- Insérer un panier de test pour le client existant
INSERT INTO cart (customer_id, creation_date, modification_date, is_active) 
SELECT id, NOW(), NOW(), TRUE 
FROM customer 
WHERE id = 1 
AND NOT EXISTS (SELECT 1 FROM cart WHERE customer_id = 1);

-- Insérer un élément de test dans le panier
INSERT INTO cart_item (cart_id, product_id, quantity)
SELECT c.id, p.id, 2
FROM cart c, product p
WHERE c.customer_id = 1 
AND p.id = 1
AND NOT EXISTS (
    SELECT 1 FROM cart_item ci 
    WHERE ci.cart_id = c.id AND ci.product_id = p.id
); 