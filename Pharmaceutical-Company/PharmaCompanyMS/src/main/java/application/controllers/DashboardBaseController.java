package application.controllers;

import application.users.CurrentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Shared logic for the dashboard screens:
 *  - greeting label showing the logged-in user's name
 *  - logout action (hard-coded FXMLLoader / Stage template)
 *
 * Each dashboard FXML must declare fx:id="welcomeLabel".
 */
public abstract class DashboardBaseController {

    @FXML protected Label welcomeLabel;

    /** Set the greeting once the FXML is loaded. */
    @FXML
    public void initialize() {
        if (welcomeLabel != null && CurrentUser.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + CurrentUser.get().getFullName());
        }
    }

    /** Log the user out and return to the login screen using the hard-coded template. */
    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        CurrentUser.clear();
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
            e.printStackTrace();
        }
    }

    /** Return the user to the role-appropriate dashboard using the hard-coded scene-switch template. */
    @FXML
    public void goBackToDashboard(ActionEvent actionEvent) {
        if (!CurrentUser.isLoggedIn()) {
            handleLogout(actionEvent);
            return;
        }
        String role = CurrentUser.get().getRole();
        String fxmlPath;
        String title;
        switch (role) {
            case application.model.common.User.ROLE_CEO:
                fxmlPath = "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEODashboardView.fxml";
                title = "CEO Dashboard";
                break;
            case application.model.common.User.ROLE_PRODUCTION_MANAGER:
                fxmlPath = "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/ProductionManagerDashboardView.fxml";
                title = "Production Manager Dashboard";
                break;
            case application.model.common.User.ROLE_ACCOUNTANT:
                fxmlPath = "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantDashboardView.fxml";
                title = "Accountant Dashboard";
                break;
            case application.model.common.User.ROLE_CUSTOMER:
                fxmlPath = "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerDashboardView.fxml";
                title = "Customer Dashboard";
                break;
            default:
                handleLogout(actionEvent);
                return;
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
            application.utilities.AlertHelper.error("Navigation Error", "Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}