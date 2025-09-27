import ui.MenuConsole;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initialisation de l'application de gestion des abonnements...");

        try {
            MenuConsole menu = new MenuConsole();
            menu.afficherMenuPrincipal();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données :");
            System.err.println("Vérifiez que PostgreSQL est démarré et que la base 'subscription_db' existe.");
            System.err.println("Détails : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}