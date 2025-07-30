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
    (1, 'Ramette Papier A4',           'PAP001', 'Papier blanc 80g/m2',      'ClaireFontaine', 'Qualité premium',
     1, 500, 5.99, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (2, 'Stylo Bille',                 'STY002', 'Stylo bille bleu',         'BIC',            'Pack de 10',
     1, 1000, 0.50, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (3, 'Café Arabica',                'CAF003', 'Café en grains',           'Lavazza',        'Torréfaction italienne',
     1, 50, 15.99, 'Kilogramme',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (4, 'Thé Vert',                    'THE004', 'Thé vert nature',          'Lipton',         'Boîte de 50 sachets',
     1, 100, 3.99, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (5, 'Nettoyant Multi-surfaces',    'NET005', 'Nettoyant universel',      'Mr Propre',      'Parfum citron',
     1, 200, 4.50, 'Liter',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (6, 'Eau de Javel',                'JAV006', 'Désinfectant puissant',    'La Croix',       'À diluer',
     1, 150, 2.99, 'Liter',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (7, 'Ruban Adhésif',               'RUB007', 'Ruban transparent',        'Scotch',         'Largeur 19 mm',
     1, 300, 1.99, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (8, 'Enveloppes A4',               'ENV008', 'Enveloppes blanches A4',   'Exacompta',      'Boîte de 100',
     1, 100, 6.99, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (9, 'Clé USB 32Go',                'USB009', 'Clé USB haute vitesse',    'SanDisk',        'USB 3.0',
     1, 80, 12.99, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

    (10, 'Piles AA',                   'PIL010', 'Pack de 4 piles AA',       'Duracell',       'Longue durée',
     1, 250, 4.99, 'Unit',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3);

-- 💰 Insertion des prix
DELETE FROM price;
ALTER TABLE price AUTO_INCREMENT = 1;

INSERT INTO price (
    id, price, measure, side,
    start_date, end_date,
    creation_date, modif_date,
    product_id, version,
    create_seller_id, modif_seller_id
) VALUES 
    (1,  5.99, 'Unit', 'Sell',
     '2024-01-01 00:00:00', '2024-12-31 23:59:59',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1, 1, 3, 3),

    (2,  0.50, 'Unit', 'Sell',
     '2024-01-01 00:00:00', '2024-12-31 23:59:59',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 2, 1, 3, 3),

    (3, 15.99, 'Kilogramme', 'Sell',
     '2024-01-01 00:00:00', '2024-12-31 23:59:59',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 1, 3, 3),

    (4,  3.99, 'Unit', 'Sell',
     '2024-01-01 00:00:00', '2024-12-31 23:59:59',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 4, 1, 3, 3),

    (5,  4.50, 'Liter', 'Sell',
     '2024-01-01 00:00:00', '2024-12-31 23:59:59',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 5, 1, 3, 3),

    (6,  2.99, 'Liter', 'Sell',
     '2024-01-01 00:00:00', '2024-12-31 23:59:59',
     '2024-01-01 00:00:00', '2024-01-01 00:00:00', 6, 1, 3, 3),

    (7,  1.99, 'Unit', 'Sell',
     CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7, 1, 3, 3),

    (8,  6.99, 'Unit', 'Sell',
     CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 8, 1, 3, 3),

    (9, 12.99, 'Unit', 'Sell',
     CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 9, 1, 3, 3),

    (10,  4.99, 'Unit', 'Sell',
     CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR),
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10, 1, 3, 3); 