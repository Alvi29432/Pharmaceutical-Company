package application.users;

import application.model.common.User;

public class CurrentUser {

    private static User current;

    private CurrentUser() {
    }

    public static void set(User user) { current = user; }
    public static User get() { return current; }
    public static void clear() { current = null; }

    public static boolean isLoggedIn() { return current != null; }
}

