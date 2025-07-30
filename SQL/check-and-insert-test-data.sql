-- Script pour vérifier et insérer les données de test
-- Exécutez ce script dans votre base de données MySQL

USE arem;

-- Vérifier si les utilisateurs de test existent
SELECT 'Vérification des utilisateurs existants...' as info;

SELECT id, email, first_name, last_name, is_admin, is_enabled 
FROM seller 
WHERE email IN ('katia@gmail.com', 'vendeuse@gmail.com', 'r.haouche@hotmail.com');

-- Si aucun utilisateur de test n'existe, les insérer
INSERT IGNORE INTO seller (
  id, address, creation_date, email, first_name, last_name, modif_date,
  phone_number, pick_name, version, password,
  is_account_non_expired, is_account_non_locked, is_admin,
  is_credentials_non_expired, is_enabled,
  create_seller_id, modif_seller_id
) VALUES 
-- Compte ADMIN (mot de passe = "admin")
(3, '12 rue du test', NOW(), 'katia@gmail.com', 'Katia', 'Admin', NOW(),
  '0600000000', 'katia-admin', '1', 
  '$2a$10$bqxG.hbD3R6WqRbV.d.htOBCf1prLsshShw3PHpj0YGnAwnFK.QK.',
  true, true, true, true, true, 1, 1),

-- Compte VENDEUSE (mot de passe = "vendeuse")
(4, '34 avenue du test', NOW(), 'vendeuse@gmail.com', 'Katia', 'Vendeuse', NOW(),
  '0600000001', 'katia-vendeuse', '1', 
  '$2a$10$JKI3JqKUyqwBoFUDmA04YedLPW5mt8CJjfbYXxKjAx7lzGu7W7kGm',
  true, true, false, true, true, 1, 1),

-- Compte ADMIN existant
(1, '53 Rue Ambroise Thomas 95100 Argenteuil', '2020-04-08 10:34:52', 'r.haouche@hotmail.com', 'ramdane', 'haouche', '2020-04-08 08:42:19', '0684099367', 'ramdane', '2', '$2a$10$c.v2ViisqB.G2s7wbq4MCu4mi2MYOe7gHZaGY7GXzN2G6oP9pouE.', true, true, true, true, true, 1, 1),

-- Compte VENDEUR existant
(2, 'Berkouka Maatkas ALGERIE', '2020-04-08 08:50:40', 'samir.khelfane@hotmail.com', 'SAMIR', 'KHELFANE', '2020-04-08 13:32:40', '00213568745', 'commerçant', '2', '$2a$10$DuIu702AyhWJfcaGlCTXjObdQklqdmgrZG4.5Rz43vhZHCXvVie2i', true, true, false, true, true, 1, 1);

-- Vérifier le résultat
SELECT 'Utilisateurs après insertion...' as info;

SELECT id, email, first_name, last_name, is_admin, is_enabled 
FROM seller 
WHERE email IN ('katia@gmail.com', 'vendeuse@gmail.com', 'r.haouche@hotmail.com', 'samir.khelfane@hotmail.com');

-- Vérifier les produits
SELECT 'Vérification des produits...' as info;
SELECT COUNT(*) as nombre_produits FROM product;

-- Si pas de produits, insérer les produits de test
INSERT IGNORE INTO product (
    id, name, reference, description, marque, comment,
    version, quantity, price, measure,
    creation_date, modif_date,
    create_seller_id, modif_seller_id
) VALUES 
(1, 'Ramette Papier A4', 'PAP001', 'Papier blanc 80g/m2', 'ClaireFontaine', 'Qualité premium',
 1, 500, 5.99, 1,
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

(2, 'Stylo Bille', 'STY002', 'Stylo bille bleu', 'BIC', 'Pack de 10',
 1, 1000, 0.50, 1,
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

(3, 'Café Arabica', 'CAF003', 'Café en grains', 'Lavazza', 'Torréfaction italienne',
 1, 50, 15.99, 2,
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

(4, 'Thé Vert', 'THE004', 'Thé vert nature', 'Lipton', 'Boîte de 50 sachets',
 1, 100, 3.99, 1,
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

(5, 'Nettoyant Multi-surfaces', 'NET005', 'Nettoyant universel', 'Mr Propre', 'Parfum citron',
 1, 200, 4.50, 3,
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3),

(6, 'Eau de Javel', 'JAV006', 'Désinfectant puissant', 'La Croix', 'À diluer',
 1, 150, 2.99, 3,
 '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3, 3);

-- Vérifier les clients
SELECT 'Vérification des clients...' as info;
SELECT COUNT(*) as nombre_clients FROM customer;

-- Si pas de clients, insérer des clients de test
INSERT IGNORE INTO customer (
    id, first_name, last_name, email, phone_number, address,
    creation_date, modification_date
) VALUES 
(1, 'Jean', 'Dupont', 'jean.dupont@email.com', '0123456789', '123 Rue de la Paix, Paris', NOW(), NOW()),
(2, 'Marie', 'Martin', 'marie.martin@email.com', '0987654321', '456 Avenue des Champs, Lyon', NOW(), NOW()),
(3, 'Pierre', 'Durand', 'pierre.durand@email.com', '0555666777', '789 Boulevard Central, Marseille', NOW(), NOW());

COMMIT;

SELECT 'Script terminé avec succès !' as info; 