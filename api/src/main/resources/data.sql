-- ==================== DONNÉES INITIALES AREM ====================

-- Insertion de l'administrateur principal
INSERT IGNORE INTO sellers (first_name, last_name, email, password, phone_number, address, is_admin, is_active) VALUES
('Admin', 'Principal', 'admin@admin.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+33123456789', '123 Rue Admin, 75001 Paris', TRUE, TRUE);

-- Insertion d'un client de test
INSERT IGNORE INTO customers (first_name, last_name, email, password, phone_number, address, is_active) VALUES
('Client', 'Test', 'essaie@client.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+33987654321', '456 Rue Client, 75002 Paris', TRUE);

-- Insertion de fournisseurs de test
INSERT IGNORE INTO providers (name, email, phone, address, contact_person, is_active) VALUES
('TechSupply Inc.', 'contact@techsupply.com', '+33145678901', '789 Avenue Tech, 75003 Paris', 'Jean Dupont', TRUE),
('Global Components', 'sales@globalcomp.com', '+33145678902', '321 Boulevard Global, 75004 Paris', 'Marie Martin', TRUE),
('Quality Materials', 'info@qualitymat.com', '+33145678903', '654 Rue Quality, 75005 Paris', 'Pierre Durand', TRUE);

-- Insertion de produits de test
INSERT IGNORE INTO products (name, reference, description, category, marque, measure, quantity, min_stock, price, cost_price, provider_id, is_active, created_by) VALUES
('Ordinateur Portable HP', 'HP-LAP-001', 'Ordinateur portable HP 15 pouces, 8GB RAM, 256GB SSD', 'Informatique', 'HP', 'Piece', 15.000, 5.000, 799.99, 599.99, 1, TRUE, 1),
('Souris Logitech', 'LOG-MOU-001', 'Souris optique Logitech sans fil', 'Informatique', 'Logitech', 'Piece', 50.000, 10.000, 29.99, 19.99, 1, TRUE, 1),
('Clavier Mécanique', 'KEY-MEC-001', 'Clavier mécanique RGB rétroéclairé', 'Informatique', 'Corsair', 'Piece', 25.000, 5.000, 129.99, 89.99, 2, TRUE, 1),
('Écran 24 pouces', 'MON-24-001', 'Écran LED 24 pouces Full HD', 'Informatique', 'Samsung', 'Piece', 12.000, 3.000, 199.99, 149.99, 2, TRUE, 1),
('Câble USB-C', 'CAB-USC-001', 'Câble USB-C 2 mètres', 'Accessoires', 'Anker', 'Piece', 100.000, 20.000, 19.99, 9.99, 3, TRUE, 1);

-- Insertion des prix actuels
INSERT IGNORE INTO prices (product_id, price_type, amount, is_current, created_by) VALUES
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
INSERT IGNORE INTO carts (customer_id, status) VALUES (1, 'ACTIVE');

-- Insertion d'articles dans le panier
INSERT IGNORE INTO cart_items (cart_id, product_id, quantity, unit_price) VALUES
(1, 2, 2.000, 29.99),
(1, 5, 1.000, 19.99);

-- Insertion de commandes de test
INSERT IGNORE INTO orders (customer_id, status, order_date, total_amount, delivery_address) VALUES
(1, 'DELIVERED', '2025-01-15 10:30:00', 829.98, '456 Rue Client, 75002 Paris'),
(1, 'PENDING', '2025-01-28 14:20:00', 169.97, '456 Rue Client, 75002 Paris');

-- Insertion d'articles de commande
INSERT IGNORE INTO order_items (order_id, product_id, quantity, unit_price, measure) VALUES
(1, 1, 1.000, 799.99, 'Piece'),
(1, 2, 1.000, 29.99, 'Piece'),
(2, 3, 1.000, 129.99, 'Piece'),
(2, 5, 2.000, 19.99, 'Piece');

-- Insertion de commandes fournisseurs
INSERT IGNORE INTO supplier_orders (provider_id, order_number, status, order_date, expected_delivery_date, total_amount, created_by) VALUES
(1, 'SUP-2025-001', 'RECEIVED', '2025-01-10 09:00:00', '2025-01-17 09:00:00', 2999.95, 1),
(2, 'SUP-2025-002', 'PENDING', '2025-01-25 11:00:00', '2025-02-01 11:00:00', 1799.94, 1);

-- Insertion d'articles de commande fournisseur
INSERT IGNORE INTO supplier_order_items (supplier_order_id, product_id, quantity_ordered, quantity_received, unit_price) VALUES
(1, 1, 5.000, 5.000, 599.99),
(1, 2, 20.000, 20.000, 19.99),
(2, 3, 10.000, 0.000, 89.99),
(2, 4, 8.000, 0.000, 149.99);

-- Insertion d'une facture de test
INSERT IGNORE INTO factures (numero_facture, order_id, customer_id, date_emission, date_echeance, montant_ht, taux_tva, montant_tva, montant_ttc, statut, notes, created_by) VALUES
('FACT-2025-001', 1, 1, '2025-01-15 10:30:00', '2025-02-14 10:30:00', 691.65, 20.00, 138.33, 829.98, 'PAYEE', 'Facture générée automatiquement pour la commande #1', 1); 