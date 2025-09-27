package dao;

import entity.*;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbonnementDAO {

    public void create(Abonnement abonnement) throws SQLException {
        String sql = "INSERT INTO abonnement (id, nom_service, montant_mensuel, date_debut, " +
                "date_fin, statut, type_abonnement, duree_engagement_mois) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, abonnement.getId());
            stmt.setString(2, abonnement.getNomService());
            stmt.setBigDecimal(3, abonnement.getMontantMensuel());
            stmt.setDate(4, Date.valueOf(abonnement.getDateDebut()));
            stmt.setDate(5, abonnement.getDateFin() != null ? Date.valueOf(abonnement.getDateFin()) : null);
            stmt.setString(6, abonnement.getStatut().name());
            stmt.setString(7, abonnement.getTypeAbonnement());

            if (abonnement instanceof AbonnementAvecEngagement) {
                stmt.setInt(8, ((AbonnementAvecEngagement) abonnement).getDureeEngagementMois());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.executeUpdate();
            System.out.println("Abonnement created successfully!");
        }
    }

    public Optional<Abonnement> findById(String id) throws SQLException {
        String sql = "SELECT * FROM abonnement WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAbonnement(rs));
            }
        }
        return Optional.empty();
    }

    public List<Abonnement> findAll() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnement ORDER BY nom_service";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

    public List<Abonnement> findActiveSubscriptions() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnement WHERE statut = 'ACTIF' ORDER BY nom_service";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

    public void update(Abonnement abonnement) throws SQLException {
        String sql = "UPDATE abonnement SET nom_service = ?, montant_mensuel = ?, " +
                "date_debut = ?, date_fin = ?, statut = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, abonnement.getNomService());
            stmt.setBigDecimal(2, abonnement.getMontantMensuel());
            stmt.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            stmt.setDate(4, abonnement.getDateFin() != null ? Date.valueOf(abonnement.getDateFin()) : null);
            stmt.setString(5, abonnement.getStatut().name());
            stmt.setString(6, abonnement.getId());

            stmt.executeUpdate();
            System.out.println("Abonnement updated successfully!");
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM abonnement WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
            System.out.println("Abonnement deleted successfully!");
        }
    }

    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        String typeAbonnement = rs.getString("type_abonnement");
        Abonnement abonnement;

        if ("AVEC_ENGAGEMENT".equals(typeAbonnement)) {
            abonnement = new AbonnementAvecEngagement();
            ((AbonnementAvecEngagement) abonnement).setDureeEngagementMois(rs.getInt("duree_engagement_mois"));
        } else {
            abonnement = new AbonnementSansEngagement();
        }

        abonnement.setId(rs.getString("id"));
        abonnement.setNomService(rs.getString("nom_service"));
        abonnement.setMontantMensuel(rs.getBigDecimal("montant_mensuel"));
        abonnement.setDateDebut(rs.getDate("date_debut").toLocalDate());

        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            abonnement.setDateFin(dateFin.toLocalDate());
        }

        abonnement.setStatut(StatutAbonnement.valueOf(rs.getString("statut")));

        return abonnement;
    }
}