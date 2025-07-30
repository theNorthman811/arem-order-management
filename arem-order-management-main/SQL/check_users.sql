-- Script pour vérifier le contenu des tables seller et customer
-- Exécute ce script dans ta base de données MySQL

USE arem;

-- ===============================================
-- 📊 VÉRIFICATION DES SELLERS (ADMINS/VENDEURS)
-- ===============================================

SELECT '=' as separator, 'VÉRIFICATION DES SELLERS' as info, '=' as separator2;

SELECT 
    id,
    email,
    first_name as prenom,
    last_name as nom,
    phone_number as telephone,
    is_admin as admin,
    is_enabled as actif,
    is_account_non_expired as compte_non_expire,
    is_account_non_locked as compte_non_verrouille,
    creation_date as date_creation
FROM seller 
ORDER BY id;

-- Compter les sellers
SELECT 
    COUNT(*) as total_sellers,
    SUM(CASE WHEN is_admin = true THEN 1 ELSE 0 END) as admins,
    SUM(CASE WHEN is_admin = false THEN 1 ELSE 0 END) as vendeurs,
    SUM(CASE WHEN is_enabled = true THEN 1 ELSE 0 END) as actifs
FROM seller;

-- ===============================================
-- 👥 VÉRIFICATION DES CUSTOMERS (CLIENTS)
-- ===============================================

SELECT '=' as separator, 'VÉRIFICATION DES CUSTOMERS' as info, '=' as separator2;

SELECT 
    id,
    email,
    first_name as prenom,
    last_name as nom,
    phone_number as telephone,
    creation_date as date_creation
FROM customer 
ORDER BY id;

-- Compter les customers
SELECT COUNT(*) as total_customers FROM customer;

-- ===============================================
-- 🔑 VÉRIFICATION DES MOTS DE PASSE HACHÉS
-- ===============================================

SELECT '=' as separator, 'MOTS DE PASSE SELLERS' as info, '=' as separator2;

SELECT 
    id,
    email,
    LEFT(password, 20) as mot_de_passe_debut,
    CHAR_LENGTH(password) as longueur_hash
FROM seller 
ORDER BY id;

SELECT '=' as separator, 'MOTS DE PASSE CUSTOMERS' as info, '=' as separator2;

SELECT 
    id,
    email,
    CASE 
        WHEN password IS NOT NULL THEN CONCAT(LEFT(password, 20), '...')
        ELSE 'NULL'
    END as mot_de_passe_debut,
    CASE 
        WHEN password IS NOT NULL THEN CHAR_LENGTH(password)
        ELSE 0
    END as longueur_hash
FROM customer 
ORDER BY id;

-- ===============================================
-- 📧 RECHERCHE PAR EMAIL SPÉCIFIQUE
-- ===============================================

SELECT '=' as separator, 'RECHERCHE EMAILS SPÉCIFIQUES' as info, '=' as separator2;

-- Vérifier katia@gmail.com
SELECT 'SELLER katia@gmail.com' as type, id, email, first_name, last_name, is_admin 
FROM seller 
WHERE email = 'katia@gmail.com'
UNION ALL
SELECT 'CUSTOMER katia@gmail.com' as type, id, email, first_name, last_name, NULL as is_admin 
FROM customer 
WHERE email = 'katia@gmail.com';

-- Vérifier r.haouche@hotmail.com
SELECT 'SELLER r.haouche@hotmail.com' as type, id, email, first_name, last_name, is_admin 
FROM seller 
WHERE email = 'r.haouche@hotmail.com'
UNION ALL
SELECT 'CUSTOMER r.haouche@hotmail.com' as type, id, email, first_name, last_name, NULL as is_admin 
FROM customer 
WHERE email = 'r.haouche@hotmail.com';

-- ===============================================
-- 🎯 RÉSUMÉ FINAL
-- ===============================================

SELECT '=' as separator, 'RÉSUMÉ' as info, '=' as separator2;

SELECT 
    'SELLERS' as table_name,
    COUNT(*) as total,
    SUM(CASE WHEN is_admin = true THEN 1 ELSE 0 END) as admins,
    GROUP_CONCAT(CONCAT(email, ' (', CASE WHEN is_admin = true THEN 'ADMIN' ELSE 'VENDEUR' END, ')') SEPARATOR ', ') as liste_emails
FROM seller
UNION ALL
SELECT 
    'CUSTOMERS' as table_name,
    COUNT(*) as total,
    0 as admins,
    GROUP_CONCAT(email SEPARATOR ', ') as liste_emails
FROM customer; 