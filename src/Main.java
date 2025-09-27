import ui.MenuConsole;
import dao.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            // Test database connection
            DatabaseConnection.getConnection();

            // Start the console application
            MenuConsole menu = new MenuConsole();
            menu.start();

        } catch (Exception e) {
            System.err.println("Erreur de démarrage: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}