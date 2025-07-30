# 🛒 AREM - Système de Gestion de Commandes

> **Système moderne de gestion de commandes et d'inventaire avec interface web élégante**

[![Angular](https://img.shields.io/badge/Angular-16+-red.svg)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1+-green.svg)](https://spring.io/projects/spring-boot)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)

## 🎯 **Vue d'ensemble**

AREM est une solution complète de gestion de commandes développée avec **Angular** (frontend) et **Spring Boot** (backend). Le système offre une interface moderne et intuitive pour gérer les produits, les commandes, les clients et les fournisseurs.

## ✨ **Fonctionnalités principales**

### 🏪 **Gestion des produits**
- ➕ Création/modification/suppression de produits
- 💰 Gestion des prix de vente et d'achat
- 📊 Suivi des stocks en temps réel
- 🚨 Alertes automatiques de stock faible
- 🏷️ Catégorisation par marque et référence

### 🛒 **Système de commandes**
- 🛍️ Panier d'achat intuitif
- 📋 Historique des commandes
- 🔄 Suivi du statut des commandes
- 💳 Processus de checkout simplifié

### 👨‍💼 **Interface administrateur**
- 📈 Dashboard avec statistiques en temps réel
- 👥 Gestion des clients
- 🏭 Gestion des fournisseurs (à venir)
- 📦 Gestion des commandes fournisseurs (à venir)
- 🔔 Système de notifications intelligentes

### 🎨 **Interface utilisateur**
- 🌟 Design moderne et responsive
- 🎪 Animations fluides
- 🔔 Notifications toast élégantes
- 📱 Compatible mobile et desktop

## 🏗️ **Architecture technique**

### **Frontend - Angular**
```
src/
├── app/
│   ├── core/              # Services partagés
│   │   ├── models/        # Modèles TypeScript
│   │   ├── services/      # Services Angular
│   │   └── guards/        # Guards de route
│   ├── features/          # Modules fonctionnels
│   │   ├── admin/         # Interface administrateur
│   │   ├── auth/          # Authentification
│   │   ├── products/      # Gestion produits
│   │   ├── cart/          # Panier
│   │   └── orders/        # Commandes
│   └── shared/            # Composants partagés
```

### **Backend - Spring Boot**
```
src/
├── api/                   # Contrôleurs REST
├── core/                  # Modèles métier
├── dataservice/           # Services de données
├── framework/             # Utilitaires
└── productInput/          # Contrats et validation
```

### **Base de données**
- 🗄️ **MySQL** pour la persistance
- 🔗 **JPA/Hibernate** pour l'ORM
- 📊 **Tables** : products, customers, orders, cart, prices

## 🚀 **Installation et démarrage**

### **Prérequis**
- ☕ Java 17+
- 📦 Node.js 18+
- 🗄️ MySQL 8.0+
- 🔧 Maven 3.6+

### **Backend (Spring Boot)**
```bash
cd api
mvn spring-boot:run
```
🌐 **API disponible sur** : `http://localhost:8080`

### **Frontend (Angular)**
```bash
cd order-management-front
npm install
ng serve
```
🌐 **Application disponible sur** : `http://localhost:4200`

### **Base de données**
```sql
-- Exécuter les scripts SQL dans l'ordre :
mysql -u root -p < SQL/orders.sql
mysql -u root -p < SQL/prices.sql
mysql -u root -p < SQL/cart_tables.sql
```

## 👥 **Comptes de test**

### **Administrateur**
- 📧 **Email** : `admin@admin.com`
- 🔐 **Mot de passe** : `admin123`

### **Client**
- 📧 **Email** : `essaie@client.com`  
- 🔐 **Mot de passe** : `client123`

## 🎯 **Roadmap**

### **🚧 En cours de développement**
- [ ] 🏭 Système complet de gestion des fournisseurs
- [ ] 📦 Commandes fournisseurs avec réception automatique
- [ ] 📊 Analytics et rapports avancés
- [ ] 🔔 Notifications push en temps réel

### **🔮 Fonctionnalités futures**
- [ ] 📱 Application mobile React Native
- [ ] 🌍 Internationalisation (i18n)
- [ ] 🔒 Authentification OAuth2
- [ ] 📈 Tableau de bord exécutif
- [ ] 🤖 IA pour prédiction des stocks

## 🛠️ **Technologies utilisées**

### **Frontend**
- 🅰️ **Angular 16+** - Framework principal
- 🎨 **SCSS** - Styling avancé
- 🔄 **RxJS** - Programmation réactive
- 📝 **TypeScript** - Typage statique
- 🎪 **CSS Animations** - Animations fluides

### **Backend**
- ☕ **Spring Boot 3.1** - Framework principal
- 🗄️ **Spring Data JPA** - Persistance
- 🔐 **Spring Security** - Sécurité
- 🎯 **Maven** - Gestion des dépendances
- 🗄️ **MySQL** - Base de données

## 📝 **API Documentation**

### **Endpoints principaux**

#### **Produits**
```http
GET    /api/v1/products        # Liste tous les produits
POST   /api/v1/product         # Créer un produit
PUT    /api/v1/product/{id}    # Modifier un produit
DELETE /api/v1/product/{id}    # Supprimer un produit
```

#### **Commandes**
```http
GET    /api/v1/orders          # Liste des commandes
POST   /api/v1/orders/checkout # Passer une commande
PUT    /api/v1/orders/{id}/status # Mettre à jour le statut
```

#### **Authentification**
```http
POST   /api/auth/login         # Connexion administrateur
POST   /api/auth/login-client  # Connexion client
POST   /api/auth/register      # Inscription client
```

## 🎨 **Captures d'écran**

### **Interface Administrateur**
![Dashboard Admin](docs/admin-dashboard.png)
*Dashboard administrateur avec statistiques en temps réel*

### **Gestion des produits**
![Gestion Produits](docs/product-management.png)
*Interface moderne de gestion des produits*

### **Notifications intelligentes**
![Notifications](docs/smart-notifications.png)
*Système de notifications avec alertes de stock*

## 🤝 **Contribution**

1. 🍴 Fork le projet
2. 🌿 Créer une branche (`git checkout -b feature/nouvelle-fonctionnalite`)
3. 💻 Committer les changements (`git commit -m 'Ajouter nouvelle fonctionnalité'`)
4. 📤 Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. 🔄 Créer une Pull Request

## 📄 **Licence**

Ce projet est sous licence **MIT** - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👨‍💻 **Développeur**

**[Ton Nom]**
- 💼 **LinkedIn** : [Ton profil LinkedIn]
- 🐦 **Twitter** : [@tonusername]
- 📧 **Email** : ton.email@exemple.com

---

<div align="center">
  
**⭐ Si ce projet t'a aidé, n'hésite pas à lui donner une étoile ! ⭐**

*Développé avec ❤️ et beaucoup de ☕*

</div> 