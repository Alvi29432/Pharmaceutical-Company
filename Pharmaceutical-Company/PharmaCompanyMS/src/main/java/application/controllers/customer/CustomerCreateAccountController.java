package application.controllers.customer;

import application.model.common.User;
import application.utilities.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomerCreateAccountController extends application.controllers.DashboardBaseController {

    @FXML private TextField    usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private TextField    fullNameField;
    @FXML private Label        infoLabel;

    @FXML
    public void handleCreate() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();
        String fullName = fullNameField.getText();

        if (username == null || username.trim().length() < 3) {
            AlertHelper.warn("Validation", "Username must be at least 3 characters.");
            return;
        }
        if (password == null || password.length() < 4) {
            AlertHelper.warn("Validation", "Password must be at least 4 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            AlertHelper.warn("Validation", "Passwords do not match.");
            return;
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Full name cannot be empty.");
            return;
        }

        List<User> users = loadUsersFromBin("users.bin");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(username.trim())) {
                AlertHelper.warn("Duplicate", "That username is already taken.");
                return;
            }
        }

        String newId = "U-" + System.currentTimeMillis();
        users.add(new User(newId, username.trim(), password, User.ROLE_CUSTOMER, fullName.trim()));
        saveUsersToBin("users.bin", users);

        AlertHelper.info("Account Created", "New customer '" + fullName.trim() + "' created.");
        infoLabel.setText("Created user ID: " + newId);
        handleReset();
    }

    @FXML
    public void handleReset() {
        usernameField.clear();
        passwordField.clear();
        confirmField.clear();
        fullNameField.clear();
    }

    @FXML
    public void goBackToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/fxml/common/LoginView.fxml"));
            Parent root = fxmlLoader.load();
            Scene loginScene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Pharma Company MS - Login");
            stage.setScene(loginScene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            AlertHelper.error("Navigation Error", "Failed to return to login screen.");
            e.printStackTrace();
        }
    }

    private List<User> loadUsersFromBin(String fileName) {
        List<User> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((User) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveUsersToBin(String fileName, List<User> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (User u : list) oos.writeObject(u);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
