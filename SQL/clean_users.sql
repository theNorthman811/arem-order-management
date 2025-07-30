-- Script de nettoyage des utilisateurs
-- Garder seulement : admin@admin.com et essaie@client.com

USE arem_db;

-- ===============================================
-- 🗑️ SUPPRESSION DES SELLERS (GARDER SEULEMENT ADMIN)
-- ===============================================

-- Vérification avant suppression
SELECT '=== SELLERS AVANT SUPPRESSION ===' as info;
SELECT id, email, first_name, last_name, is_admin FROM seller ORDER BY id;

-- Supprimer tous les sellers SAUF admin@admin.com (ID 1)
DELETE FROM seller 
WHERE email != 'admin@admin.com' 
AND id != 1;

-- Vérification après suppression
SELECT '=== SELLERS APRÈS SUPPRESSION ===' as info;
SELECT id, email, first_name, last_name, is_admin FROM seller ORDER BY id;

-- ===============================================
-- 🗑️ SUPPRESSION DES CUSTOMERS (GARDER SEULEMENT ESSAIE@CLIENT.COM)
-- ===============================================

-- Vérification avant suppression
SELECT '=== CUSTOMERS AVANT SUPPRESSION ===' as info;
SELECT id, email, first_name, last_name FROM customer WHERE email IS NOT NULL ORDER BY id;

-- Supprimer tous les customers SAUF essaie@client.com (ID 152)
DELETE FROM customer 
WHERE email != 'essaie@client.com' 
AND id != 152;

-- Vérification après suppression
SELECT '=== CUSTOMERS APRÈS SUPPRESSION ===' as info;
SELECT id, email, first_name, last_name FROM customer ORDER BY id;

-- ===============================================
-- 🧹 NETTOYAGE DES DONNÉES LIÉES (COMMANDES, PANIERS, ETC.)
-- ===============================================

-- Supprimer les commandes des utilisateurs supprimés
DELETE FROM order_item WHERE order_id IN (
    SELECT id FROM orders WHERE customer_id NOT IN (152)
);

DELETE FROM orders WHERE customer_id NOT IN (152);

-- Supprimer les paniers des utilisateurs supprimés  
DELETE FROM cart_item WHERE cart_id IN (
    SELECT id FROM cart WHERE customer_id NOT IN (152)
);

DELETE FROM cart WHERE customer_id NOT IN (152);

-- ===============================================
-- ✅ VÉRIFICATION FINALE
-- ===============================================

SELECT '=== RÉSUMÉ FINAL ===' as info;

SELECT 
    'SELLERS' as table_name,
    COUNT(*) as total,
    SUM(CASE WHEN is_admin = 1 THEN 1 ELSE 0 END) as admins,
    GROUP_CONCAT(CONCAT(email, ' (', CASE WHEN is_admin = 1 THEN 'ADMIN' ELSE 'VENDEUR' END, ')') SEPARATOR ', ') as liste_emails
FROM seller
UNION ALL
SELECT 
    'CUSTOMERS' as table_name,
    COUNT(*) as total,
    0 as admins,
    GROUP_CONCAT(email SEPARATOR ', ') as liste_emails
FROM customer
WHERE email IS NOT NULL;

-- Vérifier les tables liées
SELECT 'COMMANDES RESTANTES' as info, COUNT(*) as count FROM orders;
SELECT 'PANIERS RESTANTS' as info, COUNT(*) as count FROM cart;

SELECT '=== NETTOYAGE TERMINÉ ===' as info;

COMMIT; 