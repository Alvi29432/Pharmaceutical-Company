package application.controllers.customer;

import application.model.common.User;
import application.users.CurrentUser;
import application.utilities.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomerProfileController extends application.controllers.DashboardBaseController {

    @FXML private Label userIdLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private TextField fullNameField;
    @FXML private Label infoLabel;

    @FXML
    public void initialize() {
        handleReload();
    }

    @FXML
    public void handleReload() {
        User user = CurrentUser.get();
        if (user == null) {
            infoLabel.setText("No user logged in.");
            return;
        }
        userIdLabel.setText(user.getUserId());
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(user.getRole());
        fullNameField.setText(user.getFullName());
        infoLabel.setText("Profile loaded.");
    }

    @FXML
    public void handleSave() {
        String newName = fullNameField.getText();
        if (newName == null || newName.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Full name cannot be empty.");
            return;
        }

        User current = CurrentUser.get();
        if (current == null) {
            AlertHelper.error("Error", "No user is logged in.");
            return;
        }

        List<User> users = loadUsersFromBin("users.bin");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUserId().equals(current.getUserId())) {
                u.setFullName(newName.trim());
                users.set(i, u);
                saveUsersToBin("users.bin", users);
                CurrentUser.set(u);
                AlertHelper.info("Saved", "Profile updated successfully.");
                infoLabel.setText("Saved at " + java.time.LocalTime.now().withNano(0));
                return;
            }
        }
        AlertHelper.error("Error", "User record not found in users.bin.");
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
