package fr.ticngo.util;

import fr.ticngo.model.Administrateur;

public class SessionManager {

    private static Administrateur currentAdmin;

    private SessionManager() {}

    public static void setCurrentAdmin(Administrateur admin) {
        currentAdmin = admin;
    }

    public static Administrateur getCurrentAdmin() {
        return currentAdmin;
    }

    public static void clear() {
        currentAdmin = null;
    }

    public static boolean isLoggedIn() {
        return currentAdmin != null;
    }
}
