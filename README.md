# 📋 Système de Gestion des Abonnements

Application console Java pour gérer vos abonnements avec suivi des paiements et rapports financiers.

## 🎯 Fonctionnalités

- **Gestion d'abonnements** : Création, modification, suppression avec ou sans engagement
- **Suivi des paiements** : Génération automatique des échéances et détection des impayés
- **Rapports financiers** : Rapports mensuels, annuels et d'impayés
- **Programmation fonctionnelle** : Utilisation intensive de Stream API et lambdas

## 🏗️ Technologies

- **Java 8+** avec Stream API
- **PostgreSQL** pour la persistance
- **JDBC** pour l'accès aux données
- **Architecture en couches** (Entity, DAO, Service, UI)

## 🚀 Installation rapide

### Prérequis
- Java 8+
- PostgreSQL 12+
- Driver JDBC PostgreSQL

### Configuration

1. **Cloner le projet**
```bash
git clone https://github.com/theshamkhi/SubManager.git
cd SubManager
```

2. **Créer la base de données**


3. **Configurer la connexion** dans `DatabaseConnection.java`
```java
private static final String URL = "jdbc:postgresql://localhost:5432/subscription_db";
private static final String USERNAME = "votre_utilisateur";
private static final String PASSWORD = "votre_mot_de_passe";
```

4. **Compiler et exécuter**
```bash
javac -cp "lib/postgresql-42.7.0.jar:src" src/**/*.java -d bin/
java -cp "lib/postgresql-42.7.0.jar:bin" Main
```

## 📖 Utilisation

### Menu principal
```
==================================================
         GESTION DES ABONNEMENTS
==================================================
1.  Créer un abonnement
2.  Modifier un abonnement
3.  Supprimer un abonnement
4.  Lister tous les abonnements
5.  Afficher les paiements d'un abonnement
6.  Enregistrer un paiement
7.  Modifier un paiement
8.  Supprimer un paiement
9.  Consulter les paiements manqués
10. Afficher la somme payée d'un abonnement
11. Afficher les 5 derniers paiements
12. Générer des rapports financiers
0.  Quitter
==================================================
```

### 📋 Scénarios d'utilisation

#### Créer un abonnement Netflix
```
Choix : 1
Nom du service : Netflix Premium
Montant mensuel (€) : 17.99
Date de début (dd/MM/yyyy) : 01/01/2024
Date de fin (dd/MM/yyyy) : 01/01/2025
Type d'abonnement : 1 (Avec engagement)
Durée d'engagement (mois) : 12

✅ Abonnement créé avec succès !
📅 12 échéances générées automatiquement
```

#### Enregistrer un paiement
```
Choix : 6
=== PAIEMENTS EN ATTENTE ===
1. Netflix Premium - 17.99€ - Échéance: 01/02/2024
2. Spotify Premium - 9.99€ - Échéance: 15/02/2024

ID du paiement : 1
Type de paiement : 1 (Carte bancaire)

✅ Paiement enregistré avec succès !
```

#### Consulter les impayés
```
Choix : 9
=== PAIEMENTS MANQUÉS ===
⚠️  Netflix Premium - 17.99€ - En retard depuis 5 jours
⚠️  Spotify Premium - 9.99€ - En retard depuis 2 jours

Total impayé : 27.98€
```

#### Générer un rapport mensuel
```
Choix : 12 → 1
Mois : 3
Année : 2024

=== RAPPORT MENSUEL 03/2024 ===
Nombre de paiements : 12
Montant total payé : 189.45€
Montant moyen par paiement : 15.79€

Top 3 des abonnements :
1. Netflix Premium : 17.99€
2. Adobe Creative : 15.99€
3. Spotify Premium : 9.99€
```

#### Lister tous les abonnements
```
Choix : 4
=== MES ABONNEMENTS ===
📺 Netflix Premium - 17.99€/mois - ACTIF (Avec engagement)
🎵 Spotify Premium - 9.99€/mois - ACTIF (Sans engagement)
🎨 Adobe Creative - 15.99€/mois - SUSPENDU (Avec engagement)
☁️  Google Drive - 1.99€/mois - ACTIF (Sans engagement)

Total mensuel : 45.96€ (hors suspendus)
```

## 🛠️ Structure du projet

```
src/
├── entity/          # Entités métier (Abonnement, Paiement, enums)
├── dao/             # Accès aux données (AbonnementDAO, PaiementDAO)
├── service/         # Logique métier
├── ui/              # Interface console
└── Main.java        # Point d'entrée
```

## 🔧 Fonctionnalités avancées

- **Génération automatique d'échéances** avec Stream API
- **Détection intelligente des impayés**
- **Rapports avec agrégations complexes** utilisant Collectors
- **Gestion sûre des valeurs nulles** avec Optional
