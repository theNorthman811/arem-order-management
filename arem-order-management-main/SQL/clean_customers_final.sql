-- Script de nettoyage final des customers
-- Garder SEULEMENT essaie@client.com (ID 152)

USE arem_db;

-- ===============================================
-- 🗑️ SUPPRESSION FINALE DES CUSTOMERS
-- ===============================================

-- Vérification avant suppression
SELECT '=== CUSTOMERS AVANT NETTOYAGE FINAL ===' as info;
SELECT id, email, first_name, last_name FROM customer ORDER BY id;

-- Supprimer TOUS les customers SAUF ID 152 (essaie@client.com)
DELETE FROM customer 
WHERE id != 152;

-- Vérification après suppression
SELECT '=== CUSTOMERS APRÈS NETTOYAGE FINAL ===' as info;
SELECT id, email, first_name, last_name FROM customer ORDER BY id;

-- ===============================================
-- 🧹 NETTOYAGE DES COMMANDES ORPHELINES
-- ===============================================

-- Supprimer les items de commandes qui référencent des commandes de customers supprimés
DELETE FROM order_items WHERE order_id IN (
    SELECT id FROM orders WHERE customer_id != 152
);

-- Supprimer les commandes des customers supprimés
DELETE FROM orders WHERE customer_id != 152;

-- ===============================================
-- ✅ VÉRIFICATION FINALE COMPLÈTE
-- ===============================================

SELECT '=== RÉSUMÉ FINAL COMPLET ===' as info;

-- Vérifier sellers
SELECT 'SELLERS' as type, COUNT(*) as total FROM seller;
SELECT id, email, first_name, last_name, is_admin FROM seller;

-- Vérifier customers  
SELECT 'CUSTOMERS' as type, COUNT(*) as total FROM customer;
SELECT id, email, first_name, last_name FROM customer;

-- Vérifier données liées
SELECT 'COMMANDES' as type, COUNT(*) as total FROM orders;
SELECT 'PANIERS' as type, COUNT(*) as total FROM cart;

SELECT '=== NETTOYAGE TERMINÉ AVEC SUCCÈS ===' as info;

COMMIT; 