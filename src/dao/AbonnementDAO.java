package dao;

import entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbonnementDAO {
    private Connection connection;

    public AbonnementDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void create(Abonnement abonnement) throws SQLException {
        String sql = "INSERT INTO abonnement (id, nomService, montantMensuel, dateDebut, dateFin, statut, typeAbonnement, dureeEngagementMois) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, abonnement.getId());
            stmt.setString(2, abonnement.getNomService());
            stmt.setDouble(3, abonnement.getMontantMensuel());
            stmt.setDate(4, Date.valueOf(abonnement.getDateDebut()));
            stmt.setDate(5, abonnement.getDateFin() != null ? Date.valueOf(abonnement.getDateFin()) : null);
            stmt.setString(6, abonnement.getStatut().name());
            stmt.setString(7, abonnement.getType());

            if (abonnement instanceof AbonnementAvecEngagement) {
                stmt.setInt(8, ((AbonnementAvecEngagement) abonnement).getDureeEngagementMois());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }

    public Optional<Abonnement> findById(String id) throws SQLException {
        String sql = "SELECT * FROM abonnement WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAbonnement(rs));
            }
        }
        return Optional.empty();
    }

    public List<Abonnement> findAll() throws SQLException {
        String sql = "SELECT * FROM abonnement ORDER BY nomService";
        List<Abonnement> abonnements = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

    public List<Abonnement> findActiveSubscriptions() throws SQLException {
        String sql = "SELECT * FROM abonnement WHERE statut = 'ACTIF' ORDER BY nomService";
        List<Abonnement> abonnements = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                abonnements.add(mapResultSetToAbonnement(rs));
            }
        }
        return abonnements;
    }

    public void update(Abonnement abonnement) throws SQLException {
        String sql = "UPDATE abonnement SET nomService = ?, montantMensuel = ?, dateDebut = ?, dateFin = ?, statut = ?, dureeEngagementMois = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, abonnement.getNomService());
            stmt.setDouble(2, abonnement.getMontantMensuel());
            stmt.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            stmt.setDate(4, abonnement.getDateFin() != null ? Date.valueOf(abonnement.getDateFin()) : null);
            stmt.setString(5, abonnement.getStatut().name());

            if (abonnement instanceof AbonnementAvecEngagement) {
                stmt.setInt(6, ((AbonnementAvecEngagement) abonnement).getDureeEngagementMois());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, abonnement.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM abonnement WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        String type = rs.getString("typeAbonnement");
        Abonnement abonnement;

        if ("AVEC_ENGAGEMENT".equals(type)) {
            abonnement = new AbonnementAvecEngagement();
            ((AbonnementAvecEngagement) abonnement).setDureeEngagementMois(rs.getInt("dureeEngagementMois"));
        } else {
            abonnement = new AbonnementSansEngagement();
        }

        abonnement.setId(rs.getString("id"));
        abonnement.setNomService(rs.getString("nomService"));
        abonnement.setMontantMensuel(rs.getDouble("montantMensuel"));
        abonnement.setDateDebut(rs.getDate("dateDebut").toLocalDate());

        Date dateFin = rs.getDate("dateFin");
        if (dateFin != null) {
            abonnement.setDateFin(dateFin.toLocalDate());
        }

        abonnement.setStatut(StatutAbonnement.valueOf(rs.getString("statut")));

        return abonnement;
    }
}