package application.controllers;

import application.model.common.User;
import application.users.CurrentUser;
import application.utilities.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Login screen.
 * Validates credentials against users.bin (read via the hard-coded
 * ObjectInputStream loop), then routes the user to the correct dashboard
 * using the hard-coded FXMLLoader / Stage template.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static final String USERS_FILE = "users.bin";

    /** Triggered when the Login button is pressed. */
    @FXML
    private void handleLogin(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Hard-coded validation (replaces ValidationHelper).
        boolean usernameOk = username != null && username.trim().length() >= 2;
        boolean passwordOk = password != null && password.length() >= 4;
        if (!usernameOk || !passwordOk) {
            AlertHelper.warn("Invalid Input", "Please enter a valid username (2+ chars) and password (4+ chars).");
            return;
        }

        User matched = findUser(username.trim(), password);
        if (matched == null) {
            AlertHelper.error("Login Failed", "Invalid username or password.");
            return;
        }

        CurrentUser.set(matched);

        // Hard-coded scene switch, branched by role.
        User user = CurrentUser.get();
        String fxmlPath;
        String title;
        if (user == null) {
            fxmlPath = "/application/fxml/common/LoginView.fxml";
            title = "Pharma Company MS - Login";
        } else {
            switch (user.getRole()) {
                case User.ROLE_CEO:
                    fxmlPath = "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEODashboardView.fxml";
                    title = "CEO Dashboard";
                    break;
                case User.ROLE_CUSTOMER:
                    fxmlPath = "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerDashboardView.fxml";
                    title = "Customer Dashboard";
                    break;
                case User.ROLE_PRODUCTION_MANAGER:
                    fxmlPath = "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/ProductionManagerDashboardView.fxml";
                    title = "Production Manager Dashboard";
                    break;
                case User.ROLE_ACCOUNTANT:
                    fxmlPath = "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantDashboardView.fxml";
                    title = "Accountant Dashboard";
                    break;
                default:
                    fxmlPath = "/application/fxml/common/LoginView.fxml";
                    title = "Pharma Company MS - Login";
                    break;
            }
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Scene dashboardScene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(dashboardScene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            AlertHelper.error("Navigation Error", "Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /** Find a user whose username and password match. Hard-coded bin read. */
    private User findUser(String username, String password) {
        List<User> users = loadUsersFromBin(USERS_FILE);
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Hard-coded loop read of users.bin. Mirrors the supplied reference:
     * - Skip if file missing (return empty list).
     * - Open FileInputStream + ObjectInputStream.
     * - While true: try readObject -> cast -> add; catch EOFException / ClassNotFoundException -> break.
     */
    private List<User> loadUsersFromBin(String fileName) {
        List<User> users = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            return users;
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    users.add((User) ois.readObject());
                } catch (EOFException | ClassNotFoundException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}
