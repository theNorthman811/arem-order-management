-- Script pour ajouter les colonnes email et password à la table customer
-- Exécuter ce script dans votre base de données MySQL

USE arem_db;

-- Ajouter les colonnes email et password
ALTER TABLE customer 
ADD COLUMN email VARCHAR(255) NULL,
ADD COLUMN password VARCHAR(255) NULL;

-- Ajouter la contrainte d'unicité sur email
ALTER TABLE customer 
ADD CONSTRAINT UniqueEmail UNIQUE (email);

-- Vérifier que les colonnes ont été ajoutées
DESCRIBE customer; 