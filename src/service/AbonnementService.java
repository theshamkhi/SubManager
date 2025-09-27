package service;

import dao.AbonnementDAO;
import dao.PaiementDAO;
import entity.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbonnementService {
    private AbonnementDAO abonnementDAO;
    private PaiementDAO paiementDAO;

    public AbonnementService() throws SQLException {
        this.abonnementDAO = new AbonnementDAO();
        this.paiementDAO = new PaiementDAO();
    }

    public void creerAbonnement(Abonnement abonnement) throws SQLException {
        abonnementDAO.create(abonnement);
        genererEcheances(abonnement);
    }

    public Optional<Abonnement> trouverParId(String id) throws SQLException {
        return abonnementDAO.findById(id);
    }

    public List<Abonnement> listerTousLesAbonnements() throws SQLException {
        return abonnementDAO.findAll();
    }

    public List<Abonnement> listerAbonnementsActifs() throws SQLException {
        return abonnementDAO.findActiveSubscriptions();
    }

    public List<Abonnement> filtrerParType(String type) throws SQLException {
        return abonnementDAO.findAll().stream()
                .filter(abonnement -> abonnement.getType().equals(type))
                .collect(Collectors.toList());
    }

    public void modifierAbonnement(Abonnement abonnement) throws SQLException {
        abonnementDAO.update(abonnement);
    }

    public void resilierAbonnement(String id) throws SQLException {
        Optional<Abonnement> abonnementOpt = abonnementDAO.findById(id);
        if (abonnementOpt.isPresent()) {
            Abonnement abonnement = abonnementOpt.get();
            abonnement.setStatut(StatutAbonnement.RESILIE);
            abonnement.setDateFin(LocalDate.now());
            abonnementDAO.update(abonnement);
        }
    }

    public void supprimerAbonnement(String id) throws SQLException {
        // Supprimer d'abord les paiements associés
        List<Paiement> paiements = paiementDAO.findByAbonnement(id);
        for (Paiement paiement : paiements) {
            paiementDAO.delete(paiement.getIdPaiement());
        }
        // Puis supprimer l'abonnement
        abonnementDAO.delete(id);
    }

    private void genererEcheances(Abonnement abonnement) throws SQLException {
        LocalDate dateEcheance = abonnement.getDateDebut();
        LocalDate dateFin = abonnement.getDateFin() != null ? abonnement.getDateFin() : LocalDate.now().plusYears(1);

        while (dateEcheance.isBefore(dateFin) || dateEcheance.isEqual(dateFin)) {
            Paiement paiement = new Paiement(abonnement.getId(), dateEcheance, abonnement.getMontantMensuel());
            paiementDAO.create(paiement);
            dateEcheance = dateEcheance.plusMonths(1);
        }
    }

    public void genererNouvellesEcheances(String idAbonnement) throws SQLException {
        Optional<Abonnement> abonnementOpt = abonnementDAO.findById(idAbonnement);
        if (abonnementOpt.isPresent()) {
            Abonnement abonnement = abonnementOpt.get();

            // Trouve la dernière échéance existante
            List<Paiement> paiements = paiementDAO.findByAbonnement(idAbonnement);
            LocalDate derniereEcheance = paiements.stream()
                    .map(Paiement::getDateEcheance)
                    .max(LocalDate::compareTo)
                    .orElse(abonnement.getDateDebut().minusMonths(1));

            // Génère les prochaines échéances
            LocalDate prochaineEcheance = derniereEcheance.plusMonths(1);
            LocalDate dateFin = abonnement.getDateFin() != null ? abonnement.getDateFin() : LocalDate.now().plusYears(1);

            while (prochaineEcheance.isBefore(dateFin) || prochaineEcheance.isEqual(dateFin)) {
                Paiement paiement = new Paiement(abonnement.getId(), prochaineEcheance, abonnement.getMontantMensuel());
                paiementDAO.create(paiement);
                prochaineEcheance = prochaineEcheance.plusMonths(1);
            }
        }
    }
}