# 🚀 Guide de Déploiement AREM sur Heroku

## 📋 Prérequis
- [x] Compte Heroku créé ✅
- [x] Application Heroku créée ✅
- [x] Heroku CLI installé
- [ ] Git initialisé dans le projet

## 🎯 Étapes de Déploiement

### 1. 🔧 Configuration de la Base de Données MySQL sur Heroku

```bash
# Ajouter ClearDB MySQL (gratuit jusqu'à 5MB)
heroku addons:create cleardb:ignite -a VOTRE_NOM_APP

# Ou JawsDB MySQL (autre option)
# heroku addons:create jawsdb:kitefin -a VOTRE_NOM_APP

# Récupérer l'URL de la base de données
heroku config:get CLEARDB_DATABASE_URL -a VOTRE_NOM_APP
```

### 2. ⚙️ Configuration des Variables d'Environnement

```bash
# Configurer les variables d'environnement
heroku config:set JWT_SECRET=your-super-secret-jwt-key-here -a VOTRE_NOM_APP
heroku config:set SPRING_PROFILES_ACTIVE=prod -a VOTRE_NOM_APP

# Vérifier les variables
heroku config -a VOTRE_NOM_APP
```

### 3. 📦 Préparation du Code

```bash
# Initialiser Git si pas encore fait
git init
git add .
git commit -m "Initial commit for Heroku deployment"

# Connecter à votre app Heroku
heroku git:remote -a VOTRE_NOM_APP
```

### 4. 🏗️ Build et Déploiement

```bash
# Déployer sur Heroku
git push heroku master

# Ou si vous êtes sur une branche main
git push heroku main:master
```

### 5. 📊 Importer les Données Initiales

```bash
# Se connecter à la base de données Heroku
heroku config:get CLEARDB_DATABASE_URL -a VOTRE_NOM_APP

# Copier l'URL et remplacer mysql:// par mysql2://
# Puis exécuter vos scripts SQL:

# Option 1: Via MySQL Workbench en utilisant l'URL ClearDB
# Option 2: Via ligne de commande
mysql -h HOST -u USERNAME -p DATABASE_NAME < arem-order-management-main/SQL/orders.sql
mysql -h HOST -u USERNAME -p DATABASE_NAME < arem-order-management-main/SQL/prices.sql
mysql -h HOST -u USERNAME -p DATABASE_NAME < arem-order-management-main/SQL/cart_tables.sql
```

### 6. 🔍 Vérification et Monitoring

```bash
# Voir les logs de l'application
heroku logs --tail -a VOTRE_NOM_APP

# Ouvrir l'application
heroku open -a VOTRE_NOM_APP

# Vérifier le statut
heroku ps -a VOTRE_NOM_APP
```

## 🛠️ Dépannage

### Problèmes Courants

1. **Erreur de build Maven**
```bash
heroku config:set MAVEN_OPTS="-Xmx1024m" -a VOTRE_NOM_APP
```

2. **Problème de port**
```bash
# Vérifier que l'application utilise $PORT
heroku logs --tail -a VOTRE_NOM_APP
```

3. **Erreur de base de données**
```bash
# Vérifier la configuration de la DB
heroku config:get CLEARDB_DATABASE_URL -a VOTRE_NOM_APP
```

## 📱 Test des Endpoints

Une fois déployé, testez vos endpoints:

```bash
# Test de santé
curl https://VOTRE_APP.herokuapp.com/api/health

# Test d'authentification
curl -X POST https://VOTRE_APP.herokuapp.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"admin123"}'
```

## 🎨 Déploiement du Frontend (Optionnel)

Si vous voulez déployer le frontend Angular séparément:

```bash
cd arem-order-management-main/order-management-front
npm install
ng build --prod

# Déployer sur Netlify, Vercel ou Heroku
```

## 📝 Notes Importantes

- ✅ Le `Procfile` est déjà configuré
- ✅ Le profil de production `application-prod.properties` est créé
- ✅ La version Java 17 est spécifiée dans `system.properties`
- 🔄 Les logs sont configurés pour la production
- 🛡️ La sécurité JWT est paramétrée

## 🔗 URLs Utiles

- **Application**: `https://VOTRE_APP.herokuapp.com`
- **API**: `https://VOTRE_APP.herokuapp.com/api/v1/`
- **Dashboard Heroku**: `https://dashboard.heroku.com/apps/VOTRE_APP`
- **Logs**: `https://dashboard.heroku.com/apps/VOTRE_APP/logs`

---

🎉 **Votre application AREM est maintenant déployée sur Heroku !** 