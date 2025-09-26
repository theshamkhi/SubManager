package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/subManager";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123";

    private static Connection connection = null;

    /**
     * Get a database connection (singleton style).
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load PostgreSQL JDBC driver (not always needed in modern JDKs)
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Connexion à la base de données établie !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Pilote JDBC PostgreSQL introuvable : " + e.getMessage());
        }
        return connection;
    }

    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

    /**
     * Test if connection works.
     */
    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }
}
