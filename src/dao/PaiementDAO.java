package dao;

import entity.*;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaiementDAO {

    public void create(Paiement paiement) throws SQLException {
        String sql = "INSERT INTO paiement (id_paiement, id_abonnement, date_echeance, " +
                "date_paiement, type_paiement, statut, montant) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paiement.getIdPaiement());
            stmt.setString(2, paiement.getIdAbonnement());
            stmt.setDate(3, Date.valueOf(paiement.getDateEcheance()));
            stmt.setDate(4, paiement.getDatePaiement() != null ? Date.valueOf(paiement.getDatePaiement()) : null);
            stmt.setString(5, paiement.getTypePaiement().name());
            stmt.setString(6, paiement.getStatut().name());
            stmt.setBigDecimal(7, paiement.getMontant());

            stmt.executeUpdate();
            System.out.println("Paiement created successfully!");
        }
    }

    public Optional<Paiement> findById(String id) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE id_paiement = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPaiement(rs));
            }
        }
        return Optional.empty();
    }

    public List<Paiement> findByAbonnement(String idAbonnement) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE id_abonnement = ? ORDER BY date_echeance DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public List<Paiement> findUnpaidByAbonnement(String idAbonnement) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement " +
                "WHERE id_abonnement = ? AND statut IN ('NON_PAYE', 'EN_RETARD') " +
                "ORDER BY date_echeance";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public List<Paiement> findLastPayments(int limit) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement " +
                "WHERE statut = 'PAYE' " +
                "ORDER BY date_paiement DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public void update(Paiement paiement) throws SQLException {
        String sql = "UPDATE paiement SET date_echeance = ?, date_paiement = ?, " +
                "type_paiement = ?, statut = ?, montant = ? WHERE id_paiement = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(paiement.getDateEcheance()));
            stmt.setDate(2, paiement.getDatePaiement() != null ? Date.valueOf(paiement.getDatePaiement()) : null);
            stmt.setString(3, paiement.getTypePaiement().name());
            stmt.setString(4, paiement.getStatut().name());
            stmt.setBigDecimal(5, paiement.getMontant());
            stmt.setString(6, paiement.getIdPaiement());

            stmt.executeUpdate();
            System.out.println("Paiement updated successfully!");
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM paiement WHERE id_paiement = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
            System.out.println("Paiement deleted successfully!");
        }
    }

    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();

        paiement.setIdPaiement(rs.getString("id_paiement"));
        paiement.setIdAbonnement(rs.getString("id_abonnement"));
        paiement.setDateEcheance(rs.getDate("date_echeance").toLocalDate());

        Date datePaiement = rs.getDate("date_paiement");
        if (datePaiement != null) {
            paiement.setDatePaiement(datePaiement.toLocalDate());
        }

        paiement.setTypePaiement(TypePaiement.valueOf(rs.getString("type_paiement")));
        paiement.setStatut(StatutPaiement.valueOf(rs.getString("statut")));
        paiement.setMontant(rs.getBigDecimal("montant"));

        return paiement;
    }
}