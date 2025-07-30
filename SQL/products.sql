-- 🧹 Suppression des données existantes
DELETE FROM price;
DELETE FROM product;

-- 🚀 Réinitialisation des AUTO_INCREMENT
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE price AUTO_INCREMENT = 1;

-- 📦 Insertion des produits
INSERT INTO product (
    id, name, reference, description, marque, comment,
    version, quantity, price, measure,
    creation_date, modif_date,
    create_seller_id, modif_seller_id
) VALUES 
(1, 'Ramette Papier A4', 'PAP001', 'Papier blanc 80g/m2', 'ClaireFontaine', 'Qualité premium',
 1, 500, 5.99, 1, -- 1 = Unit
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3), -- ID 3 = Katia

(2, 'Stylo Bille', 'STY002', 'Stylo bille bleu', 'BIC', 'Pack de 10',
 1, 1000, 0.50, 1, -- 1 = Unit
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3), -- ID 3 = Katia

(3, 'Café Arabica', 'CAF003', 'Café en grains', 'Lavazza', 'Torréfaction italienne',
 1, 50, 15.99, 2, -- 2 = Kilogramme
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3), -- ID 3 = Katia

(4, 'Thé Vert', 'THE004', 'Thé vert nature', 'Lipton', 'Boîte de 50 sachets',
 1, 100, 3.99, 1, -- 1 = Unit
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3), -- ID 3 = Katia

(5, 'Nettoyant Multi-surfaces', 'NET005', 'Nettoyant universel', 'Mr Propre', 'Parfum citron',
 1, 200, 4.50, 3, -- 3 = Liter
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3), -- ID 3 = Katia

(6, 'Eau de Javel', 'JAV006', 'Désinfectant puissant', 'La Croix', 'À diluer',
 1, 150, 2.99, 3, -- 3 = Liter
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3); -- ID 3 = Katia

-- Mise à jour de la séquence
SELECT setval('product_id_seq', (SELECT MAX(id) FROM product)); 