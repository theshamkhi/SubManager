package service;

import dao.PaiementDAO;
import dao.AbonnementDAO;
import entity.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaiementService {
    private PaiementDAO paiementDAO;
    private AbonnementDAO abonnementDAO;

    public PaiementService() throws SQLException {
        this.paiementDAO = new PaiementDAO();
        this.abonnementDAO = new AbonnementDAO();
    }

    public void enregistrerPaiement(String idPaiement, TypePaiement typePaiement) throws SQLException {
        Optional<Paiement> paiementOpt = paiementDAO.findById(idPaiement);
        if (paiementOpt.isPresent()) {
            Paiement paiement = paiementOpt.get();
            paiement.setDatePaiement(LocalDate.now());
            paiement.setTypePaiement(typePaiement);
            paiement.setStatut(StatutPaiement.PAYE);
            paiementDAO.update(paiement);
        }
    }

    public void modifierPaiement(Paiement paiement) throws SQLException {
        paiementDAO.update(paiement);
    }

    public void supprimerPaiement(String idPaiement) throws SQLException {
        paiementDAO.delete(idPaiement);
    }

    public List<Paiement> listerPaiementsParAbonnement(String idAbonnement) throws SQLException {
        return paiementDAO.findByAbonnement(idAbonnement);
    }

    public List<Paiement> detecterImpayes() throws SQLException {
        List<Paiement> tousPaiements = paiementDAO.findAll();
        LocalDate aujourdhui = LocalDate.now();

        return tousPaiements.stream()
                .filter(paiement -> paiement.getStatut() != StatutPaiement.PAYE)
                .filter(paiement -> paiement.getDateEcheance().isBefore(aujourdhui))
                .peek(paiement -> {
                    if (paiement.getStatut() == StatutPaiement.NON_PAYE) {
                        paiement.setStatut(StatutPaiement.EN_RETARD);
                        try {
                            paiementDAO.update(paiement);
                        } catch (SQLException e) {
                            System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    public double calculerMontantTotalImpaye(String idAbonnement) throws SQLException {
        return paiementDAO.findUnpaidByAbonnement(idAbonnement).stream()
                .mapToDouble(Paiement::getMontant)
                .sum();
    }

    public double calculerSommePaye(String idAbonnement) throws SQLException {
        return paiementDAO.findByAbonnement(idAbonnement).stream()
                .filter(paiement -> paiement.getStatut() == StatutPaiement.PAYE)
                .mapToDouble(Paiement::getMontant)
                .sum();
    }

    public List<Paiement> listerDerniersPaiements(int nombre) throws SQLException {
        return paiementDAO.findLastPayments(nombre);
    }

    public String genererRapportMensuel(int mois, int annee) throws SQLException {
        List<Paiement> paiements = paiementDAO.findAll().stream()
                .filter(p -> p.getDatePaiement() != null)
                .filter(p -> p.getDatePaiement().getMonthValue() == mois && p.getDatePaiement().getYear() == annee)
                .collect(Collectors.toList());

        double totalPaye = paiements.stream().mapToDouble(Paiement::getMontant).sum();
        long nombrePaiements = paiements.size();

        return String.format("=== RAPPORT MENSUEL %02d/%d ===\n" +
                        "Nombre de paiements : %d\n" +
                        "Montant total payé : %.2f€\n" +
                        "Montant moyen par paiement : %.2f€\n",
                mois, annee, nombrePaiements, totalPaye,
                nombrePaiements > 0 ? totalPaye / nombrePaiements : 0);
    }

    public String genererRapportAnnuel(int annee) throws SQLException {
        List<Paiement> paiements = paiementDAO.findAll().stream()
                .filter(p -> p.getDatePaiement() != null)
                .filter(p -> p.getDatePaiement().getYear() == annee)
                .collect(Collectors.toList());

        double totalPaye = paiements.stream().mapToDouble(Paiement::getMontant).sum();

        Map<String, Double> parMois = paiements.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getDatePaiement().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                        Collectors.summingDouble(Paiement::getMontant)
                ));

        StringBuilder rapport = new StringBuilder();
        rapport.append(String.format("=== RAPPORT ANNUEL %d ===\n", annee));
        rapport.append(String.format("Montant total payé : %.2f€\n", totalPaye));
        rapport.append(String.format("Nombre total de paiements : %d\n", paiements.size()));
        rapport.append("\n--- Répartition mensuelle ---\n");

        parMois.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> rapport.append(String.format("%s : %.2f€\n", entry.getKey(), entry.getValue())));

        return rapport.toString();
    }

    public String genererRapportImpayes() throws SQLException {
        List<Paiement> impayes = detecterImpayes();

        double montantTotal = impayes.stream().mapToDouble(Paiement::getMontant).sum();

        Map<String, List<Paiement>> parAbonnement = impayes.stream()
                .collect(Collectors.groupingBy(Paiement::getIdAbonnement));

        StringBuilder rapport = new StringBuilder();
        rapport.append("=== RAPPORT DES IMPAYÉS ===\n");
        rapport.append(String.format("Nombre total d'impayés : %d\n", impayes.size()));
        rapport.append(String.format("Montant total impayé : %.2f€\n\n", montantTotal));

        parAbonnement.forEach((idAbonnement, paiementsImpayes) -> {
            try {
                Optional<Abonnement> abonnementOpt = abonnementDAO.findById(idAbonnement);
                String nomService = abonnementOpt.map(Abonnement::getNomService).orElse("Service inconnu");

                double montantAbonnement = paiementsImpayes.stream().mapToDouble(Paiement::getMontant).sum();
                rapport.append(String.format("--- %s ---\n", nomService));
                rapport.append(String.format("Impayés : %d | Montant : %.2f€\n", paiementsImpayes.size(), montantAbonnement));

                paiementsImpayes.forEach(paiement ->
                        rapport.append(String.format("  • %s - %.2f€ (%s)\n",
                                paiement.getDateEcheance(), paiement.getMontant(), paiement.getStatut())));
                rapport.append("\n");
            } catch (SQLException e) {
                rapport.append(String.format("Erreur pour l'abonnement %s\n", idAbonnement));
            }
        });

        return rapport.toString();
    }
}