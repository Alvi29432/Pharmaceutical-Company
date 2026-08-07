package application.users;

import application.model.common.User;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DefaultUserSeeder {

    private static final String USERS_FILE = "users.bin";

    private DefaultUserSeeder() {
        // utility class
    }

    public static void seedIfEmpty() {
        List<User> existing = loadUsersFromBin(USERS_FILE);
        if (existing == null) {
            existing = new ArrayList<>();
        }
        if (existing.isEmpty()) {
            List<User> defaults = new ArrayList<>();
            defaults.add(new User("U-001", "ceo",       "ceo123",  User.ROLE_CEO,                "Chief Executive Officer"));
            defaults.add(new User("U-002", "customer",  "cust123", User.ROLE_CUSTOMER,           "Default Customer"));
            defaults.add(new User("U-003", "pm",        "pm123",   User.ROLE_PRODUCTION_MANAGER, "Production Manager"));
            defaults.add(new User("U-004", "acc",       "acc123",  User.ROLE_ACCOUNTANT,         "Accountant"));
            saveUsersToBin(USERS_FILE, defaults);
        }
    }

    private static List<User> loadUsersFromBin(String filename) {
        List<User> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof User) out.add((User) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private static void saveUsersToBin(String filename, List<User> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (User item : list) oos.writeObject(item);
        } catch (IOException ignored) {}
    }
}

