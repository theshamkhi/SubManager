package dao;

import entity.Paiement;
import entity.StatutPaiement;
import entity.TypePaiement;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaiementDAO {
    private Connection connection;

    public PaiementDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void create(Paiement paiement) throws SQLException {
        String sql = "INSERT INTO paiement (idPaiement, idAbonnement, dateEcheance, datePaiement, typePaiement, statut, montant) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paiement.getIdPaiement());
            stmt.setString(2, paiement.getIdAbonnement());
            stmt.setDate(3, Date.valueOf(paiement.getDateEcheance()));
            stmt.setDate(4, paiement.getDatePaiement() != null ? Date.valueOf(paiement.getDatePaiement()) : null);
            stmt.setString(5, paiement.getTypePaiement() != null ? paiement.getTypePaiement().name() : null);
            stmt.setString(6, paiement.getStatut().name());
            stmt.setDouble(7, paiement.getMontant());

            stmt.executeUpdate();
        }
    }

    public Optional<Paiement> findById(String id) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE idPaiement = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPaiement(rs));
            }
        }
        return Optional.empty();
    }

    public List<Paiement> findByAbonnement(String idAbonnement) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE idAbonnement = ? ORDER BY dateEcheance DESC";
        List<Paiement> paiements = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public List<Paiement> findAll() throws SQLException {
        String sql = "SELECT * FROM paiement ORDER BY dateEcheance DESC";
        List<Paiement> paiements = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public List<Paiement> findUnpaidByAbonnement(String idAbonnement) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE idAbonnement = ? AND statut IN ('NON_PAYE', 'EN_RETARD') ORDER BY dateEcheance";
        List<Paiement> paiements = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idAbonnement);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public List<Paiement> findLastPayments(int limit) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE datePaiement IS NOT NULL ORDER BY datePaiement DESC LIMIT ?";
        List<Paiement> paiements = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    public void update(Paiement paiement) throws SQLException {
        String sql = "UPDATE paiement SET dateEcheance = ?, datePaiement = ?, typePaiement = ?, statut = ?, montant = ? WHERE idPaiement = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(paiement.getDateEcheance()));
            stmt.setDate(2, paiement.getDatePaiement() != null ? Date.valueOf(paiement.getDatePaiement()) : null);
            stmt.setString(3, paiement.getTypePaiement() != null ? paiement.getTypePaiement().name() : null);
            stmt.setString(4, paiement.getStatut().name());
            stmt.setDouble(5, paiement.getMontant());
            stmt.setString(6, paiement.getIdPaiement());

            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM paiement WHERE idPaiement = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();

        paiement.setIdPaiement(rs.getString("idPaiement"));
        paiement.setIdAbonnement(rs.getString("idAbonnement"));
        paiement.setDateEcheance(rs.getDate("dateEcheance").toLocalDate());

        Date datePaiement = rs.getDate("datePaiement");
        if (datePaiement != null) {
            paiement.setDatePaiement(datePaiement.toLocalDate());
        }

        String typePaiement = rs.getString("typePaiement");
        if (typePaiement != null) {
            paiement.setTypePaiement(TypePaiement.valueOf(typePaiement));
        }

        paiement.setStatut(StatutPaiement.valueOf(rs.getString("statut")));
        paiement.setMontant(rs.getDouble("montant"));

        return paiement;
    }
}