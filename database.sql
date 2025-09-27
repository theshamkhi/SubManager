-- Création de la base de données
CREATE DATABASE subscription_db;

-- Utiliser la base de données
\c subscription_db;

-- Création de la table abonnement
CREATE TABLE IF NOT EXISTS abonnement (
                                          id VARCHAR(36) PRIMARY KEY,
    nomService VARCHAR(100) NOT NULL,
    montantMensuel DECIMAL(10,2) NOT NULL CHECK (montantMensuel > 0),
    dateDebut DATE NOT NULL,
    dateFin DATE,
    statut VARCHAR(20) NOT NULL CHECK (statut IN ('ACTIF', 'SUSPENDU', 'RESILIE')),
    typeAbonnement VARCHAR(20) NOT NULL CHECK (typeAbonnement IN ('AVEC_ENGAGEMENT', 'SANS_ENGAGEMENT')),
    dureeEngagementMois INTEGER CHECK (dureeEngagementMois > 0),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Création de la table paiement
CREATE TABLE IF NOT EXISTS paiement (
                                        idPaiement VARCHAR(36) PRIMARY KEY,
    idAbonnement VARCHAR(36) NOT NULL,
    dateEcheance DATE NOT NULL,
    datePaiement DATE,
    typePaiement VARCHAR(20) CHECK (typePaiement IN ('CARTE_BANCAIRE', 'VIREMENT', 'PRELEVEMENT', 'PAYPAL', 'ESPECES')),
    statut VARCHAR(20) NOT NULL CHECK (statut IN ('PAYE', 'NON_PAYE', 'EN_RETARD')),
    montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idAbonnement) REFERENCES abonnement(id) ON DELETE CASCADE
    );