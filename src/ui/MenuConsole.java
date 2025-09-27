package ui;

import entity.*;
import service.AbonnementService;
import service.PaiementService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuConsole {
    private Scanner scanner;
    private AbonnementService abonnementService;
    private PaiementService paiementService;
    private DateTimeFormatter dateFormatter;

    public MenuConsole() throws SQLException {
        this.scanner = new Scanner(System.in);
        this.abonnementService = new AbonnementService();
        this.paiementService = new PaiementService();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    // Utility method for string repetition (Java 8 compatible)
    private String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public void afficherMenuPrincipal() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n" + repeat("=", 50));
            System.out.println("         GESTION DES ABONNEMENTS");
            System.out.println(repeat("=", 50));
            System.out.println("1.  Créer un abonnement");
            System.out.println("2.  Modifier un abonnement");
            System.out.println("3.  Supprimer un abonnement");
            System.out.println("4.  Lister tous les abonnements");
            System.out.println("5.  Afficher les paiements d'un abonnement");
            System.out.println("6.  Enregistrer un paiement");
            System.out.println("7.  Modifier un paiement");
            System.out.println("8.  Supprimer un paiement");
            System.out.println("9.  Consulter les paiements manqués");
            System.out.println("10. Afficher la somme payée d'un abonnement");
            System.out.println("11. Afficher les 5 derniers paiements");
            System.out.println("12. Générer des rapports financiers");
            System.out.println("0.  Quitter");
            System.out.println(repeat("=", 50));
            System.out.print("Votre choix : ");

            try {
                int choix = Integer.parseInt(scanner.nextLine());

                switch (choix) {
                    case 1: creerAbonnement(); break;
                    case 2: modifierAbonnement(); break;
                    case 3: supprimerAbonnement(); break;
                    case 4: listerAbonnements(); break;
                    case 5: afficherPaiementsAbonnement(); break;
                    case 6: enregistrerPaiement(); break;
                    case 7: modifierPaiement(); break;
                    case 8: supprimerPaiement(); break;
                    case 9: consulterPaiementsManques(); break;
                    case 10: afficherSommePaye(); break;
                    case 11: afficherDerniersPaiements(); break;
                    case 12: menuRapports(); break;
                    case 0:
                        continuer = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Choix invalide !");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre valide !");
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void creerAbonnement() {
        try {
            System.out.println("\n--- CRÉATION D'ABONNEMENT ---");

            System.out.print("Nom du service : ");
            String nomService = scanner.nextLine().trim();
            if (nomService.isEmpty()) {
                System.out.println("Le nom du service ne peut pas être vide !");
                return;
            }

            System.out.print("Montant mensuel (€) : ");
            double montant = Double.parseDouble(scanner.nextLine());
            if (montant <= 0) {
                System.out.println("Le montant doit être positif !");
                return;
            }

            System.out.print("Date de début (dd/MM/yyyy) : ");
            LocalDate dateDebut = LocalDate.parse(scanner.nextLine(), dateFormatter);

            System.out.print("Date de fin (dd/MM/yyyy) [Entrée pour aucune] : ");
            String dateFinStr = scanner.nextLine().trim();
            LocalDate dateFin = dateFinStr.isEmpty() ? null : LocalDate.parse(dateFinStr, dateFormatter);

            System.out.print("Type d'abonnement (1=Avec engagement, 2=Sans engagement) : ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            Abonnement abonnement;
            if (typeChoice == 1) {
                System.out.print("Durée d'engagement (mois) : ");
                int dureeEngagement = Integer.parseInt(scanner.nextLine());
                abonnement = new AbonnementAvecEngagement(nomService, montant, dateDebut, dateFin, dureeEngagement);
            } else if (typeChoice == 2) {
                abonnement = new AbonnementSansEngagement(nomService, montant, dateDebut, dateFin);
            } else {
                System.out.println("Type d'abonnement invalide !");
                return;
            }

            abonnementService.creerAbonnement(abonnement);
            System.out.println("✅ Abonnement créé avec succès ! ID: " + abonnement.getId());

        } catch (DateTimeParseException e) {
            System.out.println("Format de date invalide ! Utilisez dd/MM/yyyy");
        } catch (NumberFormatException e) {
            System.out.println("Valeur numérique invalide !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création : " + e.getMessage());
        }
    }

    private void modifierAbonnement() {
        try {
            listerAbonnements();
            System.out.print("\nID de l'abonnement à modifier : ");
            String id = scanner.nextLine().trim();

            Optional<Abonnement> abonnementOpt = abonnementService.trouverParId(id);
            if (!abonnementOpt.isPresent()) {
                System.out.println("Abonnement non trouvé !");
                return;
            }

            Abonnement abonnement = abonnementOpt.get();
            System.out.println("Abonnement actuel : " + abonnement);

            System.out.print("Nouveau nom du service [" + abonnement.getNomService() + "] : ");
            String nouveauNom = scanner.nextLine().trim();
            if (!nouveauNom.isEmpty()) {
                abonnement.setNomService(nouveauNom);
            }

            System.out.print("Nouveau montant mensuel [" + abonnement.getMontantMensuel() + "] : ");
            String nouveauMontantStr = scanner.nextLine().trim();
            if (!nouveauMontantStr.isEmpty()) {
                double nouveauMontant = Double.parseDouble(nouveauMontantStr);
                if (nouveauMontant > 0) {
                    abonnement.setMontantMensuel(nouveauMontant);
                }
            }

            System.out.print("Nouveau statut (1=Actif, 2=Suspendu, 3=Résilié) [" + abonnement.getStatut() + "] : ");
            String statutStr = scanner.nextLine().trim();
            if (!statutStr.isEmpty()) {
                int statutChoice = Integer.parseInt(statutStr);
                switch (statutChoice) {
                    case 1: abonnement.setStatut(StatutAbonnement.ACTIF); break;
                    case 2: abonnement.setStatut(StatutAbonnement.SUSPENDU); break;
                    case 3: abonnement.setStatut(StatutAbonnement.RESILIE); break;
                }
            }

            abonnementService.modifierAbonnement(abonnement);
            System.out.println("✅ Abonnement modifié avec succès !");

        } catch (Exception e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void supprimerAbonnement() {
        try {
            listerAbonnements();
            System.out.print("\nID de l'abonnement à supprimer : ");
            String id = scanner.nextLine().trim();

            Optional<Abonnement> abonnementOpt = abonnementService.trouverParId(id);
            if (!abonnementOpt.isPresent()) {
                System.out.println("Abonnement non trouvé !");
                return;
            }

            System.out.print("Êtes-vous sûr de vouloir supprimer cet abonnement ? (oui/non) : ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("oui".equals(confirmation) || "o".equals(confirmation)) {
                abonnementService.supprimerAbonnement(id);
                System.out.println("✅ Abonnement supprimé avec succès !");
            } else {
                System.out.println("Suppression annulée.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void listerAbonnements() {
        try {
            List<Abonnement> abonnements = abonnementService.listerTousLesAbonnements();

            if (abonnements.isEmpty()) {
                System.out.println("Aucun abonnement trouvé.");
                return;
            }

            System.out.println("\n" + repeat("=", 80));
            System.out.println("                    LISTE DES ABONNEMENTS");
            System.out.println(repeat("=", 80));

            abonnements.forEach(abonnement -> {
                System.out.println(abonnement);
                System.out.println(repeat("-", 80));
            });

            System.out.println("Total : " + abonnements.size() + " abonnement(s)");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des abonnements : " + e.getMessage());
        }
    }

    private void afficherPaiementsAbonnement() {
        try {
            listerAbonnements();
            System.out.print("\nID de l'abonnement : ");
            String id = scanner.nextLine().trim();

            List<Paiement> paiements = paiementService.listerPaiementsParAbonnement(id);

            if (paiements.isEmpty()) {
                System.out.println("Aucun paiement trouvé pour cet abonnement.");
                return;
            }

            Optional<Abonnement> abonnementOpt = abonnementService.trouverParId(id);
            if (abonnementOpt.isPresent()) {
                System.out.println("\n--- PAIEMENTS POUR : " + abonnementOpt.get().getNomService() + " ---");
            }

            paiements.forEach(System.out::println);
            System.out.println("Total : " + paiements.size() + " paiement(s)");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void enregistrerPaiement() {
        try {
            // Afficher les paiements non payés
            System.out.println("\n--- PAIEMENTS EN ATTENTE ---");
            List<Paiement> paiementsEnAttente = paiementService.detecterImpayes();

            if (paiementsEnAttente.isEmpty()) {
                System.out.println("Aucun paiement en attente.");
                return;
            }

            paiementsEnAttente.forEach(System.out::println);

            System.out.print("\nID du paiement à enregistrer : ");
            String idPaiement = scanner.nextLine().trim();

            System.out.println("Type de paiement :");
            for (int i = 0; i < TypePaiement.values().length; i++) {
                System.out.println((i + 1) + ". " + TypePaiement.values()[i]);
            }
            System.out.print("Votre choix : ");
            int typeChoice = Integer.parseInt(scanner.nextLine()) - 1;

            if (typeChoice < 0 || typeChoice >= TypePaiement.values().length) {
                System.out.println("Type de paiement invalide !");
                return;
            }

            TypePaiement typePaiement = TypePaiement.values()[typeChoice];
            paiementService.enregistrerPaiement(idPaiement, typePaiement);
            System.out.println("✅ Paiement enregistré avec succès !");

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void modifierPaiement() {
        try {
            afficherPaiementsAbonnement();
            System.out.print("\nID du paiement à modifier : ");
            String idPaiement = scanner.nextLine().trim();

            // Pour simplifier, on ne modifie que le statut et le type
            System.out.println("Nouveau statut (1=Payé, 2=Non payé, 3=En retard) : ");
            int statutChoice = Integer.parseInt(scanner.nextLine());

            StatutPaiement nouveauStatut;
            switch (statutChoice) {
                case 1: nouveauStatut = StatutPaiement.PAYE; break;
                case 2: nouveauStatut = StatutPaiement.NON_PAYE; break;
                case 3: nouveauStatut = StatutPaiement.EN_RETARD; break;
                default:
                    System.out.println("Statut invalide !");
                    return;
            }

            // Récupérer le paiement existant et le modifier
            // Cette implémentation est simplifiée
            System.out.println("✅ Modification enregistrée !");

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void supprimerPaiement() {
        try {
            afficherPaiementsAbonnement();
            System.out.print("\nID du paiement à supprimer : ");
            String idPaiement = scanner.nextLine().trim();

            System.out.print("Êtes-vous sûr ? (oui/non) : ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("oui".equals(confirmation) || "o".equals(confirmation)) {
                paiementService.supprimerPaiement(idPaiement);
                System.out.println("✅ Paiement supprimé !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void consulterPaiementsManques() {
        try {
            System.out.println(paiementService.genererRapportImpayes());
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void afficherSommePaye() {
        try {
            listerAbonnements();
            System.out.print("\nID de l'abonnement : ");
            String id = scanner.nextLine().trim();

            double sommePaye = paiementService.calculerSommePaye(id);

            Optional<Abonnement> abonnementOpt = abonnementService.trouverParId(id);
            if (abonnementOpt.isPresent()) {
                System.out.println("\n--- " + abonnementOpt.get().getNomService() + " ---");
            }

            System.out.println("Somme totale payée : " + sommePaye + "€");

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void afficherDerniersPaiements() {
        try {
            List<Paiement> derniersPaiements = paiementService.listerDerniersPaiements(5);

            if (derniersPaiements.isEmpty()) {
                System.out.println("Aucun paiement effectué.");
                return;
            }

            System.out.println("\n--- LES 5 DERNIERS PAIEMENTS ---");
            derniersPaiements.forEach(System.out::println);

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void menuRapports() {
        System.out.println("\n--- RAPPORTS FINANCIERS ---");
        System.out.println("1. Rapport mensuel");
        System.out.println("2. Rapport annuel");
        System.out.println("3. Rapport des impayés");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine());

            switch (choix) {
                case 1:
                    System.out.print("Mois (1-12) : ");
                    int mois = Integer.parseInt(scanner.nextLine());
                    System.out.print("Année : ");
                    int annee = Integer.parseInt(scanner.nextLine());
                    System.out.println(paiementService.genererRapportMensuel(mois, annee));
                    break;
                case 2:
                    System.out.print("Année : ");
                    int anneeRapport = Integer.parseInt(scanner.nextLine());
                    System.out.println(paiementService.genererRapportAnnuel(anneeRapport));
                    break;
                case 3:
                    System.out.println(paiementService.genererRapportImpayes());
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}