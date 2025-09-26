package ui;

import service.AbonnementService;
import service.PaiementService;
import entity.*;
import java.math.BigDecimal;
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

    public MenuConsole() {
        this.scanner = new Scanner(System.in);
        this.abonnementService = new AbonnementService();
        this.paiementService = new PaiementService();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void start() {
        System.out.println("=== GESTIONNAIRE D'ABONNEMENTS ===");

        while (true) {
            afficherMenuPrincipal();
            int choix = lireChoix();

            try {
                switch (choix) {
                    case 1:
                        menuAbonnements();
                        break;
                    case 2:
                        menuPaiements();
                        break;
                    case 3:
                        menuRapports();
                        break;
                    case 0:
                        System.out.println("Au revoir !");
                        return;
                    default:
                        System.out.println("Choix invalide. Réessayez.");
                }

            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    private void afficherMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Gestion des abonnements");
        System.out.println("2. Gestion des paiements");
        System.out.println("3. Rapports et statistiques");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }

    private void menuAbonnements() {
        while (true) {
            System.out.println("\n=== GESTION ABONNEMENTS ===");
            System.out.println("1. Créer un abonnement");
            System.out.println("2. Lister tous les abonnements");
            System.out.println("3. Modifier un abonnement");
            System.out.println("4. Supprimer un abonnement");
            System.out.println("5. Résilier un abonnement");
            System.out.println("6. Voir détails d'un abonnement");
            System.out.println("0. Retour menu principal");
            System.out.print("Votre choix: ");

            int choix = lireChoix();

            try {
                switch (choix) {
                    case 1:
                        creerAbonnement();
                        break;
                    case 2:
                        listerAbonnements();
                        break;
                    case 3:
                        modifierAbonnement();
                        break;
                    case 4:
                        supprimerAbonnement();
                        break;
                    case 5:
                        resilierAbonnement();
                        break;
                    case 6:
                        voirDetailsAbonnement();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    private void menuPaiements() {
        while (true) {
            System.out.println("\n=== GESTION PAIEMENTS ===");
            System.out.println("1. Enregistrer un paiement");
            System.out.println("2. Voir paiements d'un abonnement");
            System.out.println("3. Voir paiements impayés");
            System.out.println("4. Voir somme payée d'un abonnement");
            System.out.println("5. Voir les 5 derniers paiements");
            System.out.println("6. Détecter les paiements en retard");
            System.out.println("0. Retour menu principal");
            System.out.print("Votre choix: ");

            int choix = lireChoix();

            try {
                switch (choix) {
                    case 1:
                        enregistrerPaiement();
                        break;
                    case 2:
                        voirPaiementsAbonnement();
                        break;
                    case 3:
                        voirPaiementsImpayes();
                        break;
                    case 4:
                        voirSommePaye();
                        break;
                    case 5:
                        voirDerniersPaiements();
                        break;
                    case 6:
                        detecterRetards();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    private void menuRapports() {
        while (true) {
            System.out.println("\n=== RAPPORTS ===");
            System.out.println("1. Rapport mensuel");
            System.out.println("2. Rapport annuel");
            System.out.println("3. Rapport des impayés");
            System.out.println("0. Retour menu principal");
            System.out.print("Votre choix: ");

            int choix = lireChoix();

            try {
                switch (choix) {
                    case 1:
                        genererRapportMensuel();
                        break;
                    case 2:
                        genererRapportAnnuel();
                        break;
                    case 3:
                        genererRapportImpayes();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    // Abonnement methods
    private void creerAbonnement() throws Exception {
        System.out.println("\n=== CRÉER ABONNEMENT ===");

        System.out.print("Nom du service: ");
        String nomService = scanner.nextLine();

        System.out.print("Montant mensuel (€): ");
        BigDecimal montant = new BigDecimal(scanner.nextLine());

        System.out.print("Date de début (dd/MM/yyyy): ");
        LocalDate dateDebut = LocalDate.parse(scanner.nextLine(), dateFormatter);

        System.out.print("Type d'abonnement (1: Avec engagement, 2: Sans engagement): ");
        int typeChoix = lireChoix();

        Abonnement abonnement;

        if (typeChoix == 1) {
            System.out.print("Durée d'engagement (mois): ");
            int duree = lireChoix();
            abonnement = new AbonnementAvecEngagement(nomService, montant, dateDebut, duree);
        } else {
            abonnement = new AbonnementSansEngagement(nomService, montant, dateDebut);
        }

        abonnementService.createAbonnement(abonnement);
        System.out.println("Abonnement créé avec succès!");
    }

    private void listerAbonnements() throws Exception {
        System.out.println("\n=== LISTE DES ABONNEMENTS ===");
        List<Abonnement> abonnements = abonnementService.getAllAbonnements();

        if (abonnements.isEmpty()) {
            System.out.println("Aucun abonnement trouvé.");
            return;
        }

        for (Abonnement a : abonnements) {
            System.out.println("\nID: " + a.getId());
            System.out.println("Service: " + a.getNomService());
            System.out.println("Montant: " + a.getMontantMensuel() + "€/mois");
            System.out.println("Statut: " + a.getStatut());
            System.out.println("Type: " + a.getTypeAbonnement());
            System.out.println("-----");
        }
    }

    private void voirDetailsAbonnement() throws Exception {
        System.out.print("ID de l'abonnement: ");
        String id = scanner.nextLine();

        Optional<Abonnement> abonnementOpt = abonnementService.findById(id);
        if (abonnementOpt.isPresent()) {
            abonnementService.displayAbonnementSummary(abonnementOpt.get());
        } else {
            System.out.println("Abonnement non trouvé.");
        }
    }

    private void modifierAbonnement() throws Exception {
        System.out.print("ID de l'abonnement à modifier: ");
        String id = scanner.nextLine();

        Optional<Abonnement> abonnementOpt = abonnementService.findById(id);
        if (abonnementOpt.isPresent()) {
            Abonnement abonnement = abonnementOpt.get();

            System.out.print("Nouveau nom du service (actuel: " + abonnement.getNomService() + "): ");
            String nouveauNom = scanner.nextLine();
            if (!nouveauNom.trim().isEmpty()) {
                abonnement.setNomService(nouveauNom);
            }

            System.out.print("Nouveau montant (actuel: " + abonnement.getMontantMensuel() + "): ");
            String nouveauMontant = scanner.nextLine();
            if (!nouveauMontant.trim().isEmpty()) {
                abonnement.setMontantMensuel(new BigDecimal(nouveauMontant));
            }

            abonnementService.updateAbonnement(abonnement);
            System.out.println("Abonnement modifié avec succès!");
        } else {
            System.out.println("Abonnement non trouvé.");
        }
    }

    private void supprimerAbonnement() throws Exception {
        System.out.print("ID de l'abonnement à supprimer: ");
        String id = scanner.nextLine();

        System.out.print("Êtes-vous sûr ? (oui/non): ");
        String confirmation = scanner.nextLine();

        if ("oui".equalsIgnoreCase(confirmation)) {
            abonnementService.deleteAbonnement(id);
        } else {
            System.out.println("Suppression annulée.");
        }
    }

    private void resilierAbonnement() throws Exception {
        System.out.print("ID de l'abonnement à résilier: ");
        String id = scanner.nextLine();

        abonnementService.resilierAbonnement(id);
    }

    // Payment methods
    private void enregistrerPaiement() throws Exception {
        System.out.println("\n=== ENREGISTRER PAIEMENT ===");

        // First, show unpaid payments
        List<Paiement> impayes = paiementService.getPaiementsImpayes();
        if (impayes.isEmpty()) {
            System.out.println("Aucun paiement en attente.");
            return;
        }

        System.out.println("Paiements en attente:");
        for (int i = 0; i < impayes.size(); i++) {
            Paiement p = impayes.get(i);
            System.out.println((i+1) + ". " + p.getIdPaiement() + " - Échéance: " +
                    p.getDateEcheance().format(dateFormatter) + " - Montant: " + p.getMontant() + "€");
        }

        System.out.print("Sélectionnez un paiement (numéro): ");
        int choix = lireChoix() - 1;

        if (choix >= 0 && choix < impayes.size()) {
            Paiement paiement = impayes.get(choix);

            System.out.print("Date du paiement (dd/MM/yyyy) - Entrée pour aujourd'hui: ");
            String dateStr = scanner.nextLine();
            LocalDate datePaiement = dateStr.trim().isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr, dateFormatter);

            System.out.println("Type de paiement:");
            for (int i = 0; i < TypePaiement.values().length; i++) {
                System.out.println((i+1) + ". " + TypePaiement.values()[i]);
            }
            System.out.print("Votre choix: ");
            int typeChoix = lireChoix() - 1;

            TypePaiement typePaiement = TypePaiement.values()[typeChoix];

            paiementService.enregistrerPaiement(paiement.getIdPaiement(), datePaiement, typePaiement);
        } else {
            System.out.println("Choix invalide.");
        }
    }

    private void voirPaiementsAbonnement() throws Exception {
        System.out.print("ID de l'abonnement: ");
        String id = scanner.nextLine();

        List<Paiement> paiements = paiementService.getPaiementsParAbonnement(id);

        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement trouvé pour cet abonnement.");
            return;
        }

        System.out.println("\n=== PAIEMENTS DE L'ABONNEMENT ===");
        for (Paiement p : paiements) {
            System.out.println("Échéance: " + p.getDateEcheance().format(dateFormatter) +
                    " - Montant: " + p.getMontant() + "€ - Statut: " + p.getStatut());
            if (p.getDatePaiement() != null) {
                System.out.println("  Payé le: " + p.getDatePaiement().format(dateFormatter));
            }
            System.out.println("-----");
        }
    }

    private void voirPaiementsImpayes() throws Exception {
        System.out.println("\n=== PAIEMENTS IMPAYÉS ===");
        List<Paiement> impayes = paiementService.getPaiementsImpayes();

        if (impayes.isEmpty()) {
            System.out.println("Aucun paiement impayé.");
            return;
        }

        BigDecimal totalImpaye = BigDecimal.ZERO;
        for (Paiement p : impayes) {
            System.out.println("ID: " + p.getIdPaiement());
            System.out.println("Échéance: " + p.getDateEcheance().format(dateFormatter));
            System.out.println("Montant: " + p.getMontant() + "€");
            System.out.println("Statut: " + p.getStatut());
            totalImpaye = totalImpaye.add(p.getMontant());
            System.out.println("-----");
        }

        System.out.println("TOTAL IMPAYÉ: " + totalImpaye + "€");
    }

    private void voirSommePaye() throws Exception {
        System.out.print("ID de l'abonnement: ");
        String id = scanner.nextLine();

        BigDecimal somme = paiementService.getSommePaye(id);
        System.out.println("Somme totale payée: " + somme + "€");
    }

    private void voirDerniersPaiements() throws Exception {
        System.out.println("\n=== 5 DERNIERS PAIEMENTS ===");
        List<Paiement> derniers = paiementService.getDerniersPaiements(5);

        for (Paiement p : derniers) {
            System.out.println("Date: " + p.getDatePaiement().format(dateFormatter) +
                    " - Montant: " + p.getMontant() + "€ - Type: " + p.getTypePaiement());
        }
    }

    private void detecterRetards() throws Exception {
        paiementService.detecterImpayes();
    }

    // Report methods
    private void genererRapportMensuel() throws Exception {
        System.out.print("Mois (1-12): ");
        int mois = lireChoix();
        System.out.print("Année: ");
        int annee = lireChoix();

        paiementService.generateRapportMensuel(mois, annee);
    }

    private void genererRapportAnnuel() throws Exception {
        System.out.print("Année: ");
        int annee = lireChoix();

        paiementService.generateRapportAnnuel(annee);
    }

    private void genererRapportImpayes() throws Exception {
        voirPaiementsImpayes();
    }

    // Utility methods
    private int lireChoix() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}