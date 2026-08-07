package application.controllers.chiefexecutiveofficer;

import application.controllers.DashboardBaseController;
import application.utilities.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CEODashboardController extends DashboardBaseController {

    @FXML public void showDashboardStats(ActionEvent actionEvent)    { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEODashboardStatsView.fxml", "CEO Dashboard Stats"); }
    @FXML public void showPolicyApproval(ActionEvent actionEvent)    { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOPolicyApprovalView.fxml", "CEO Policy Approval"); }
    @FXML public void showBudgetApproval(ActionEvent actionEvent)    { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOBudgetApprovalView.fxml", "CEO Budget Approval"); }
    @FXML public void showInventory(ActionEvent actionEvent)         { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOInventoryView.fxml", "CEO Inventory"); }
    @FXML public void showProduction(ActionEvent actionEvent)        { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOProductionView.fxml", "CEO Production"); }
    @FXML public void showCustomerSatisfaction(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOCustomerSatisfactionView.fxml", "CEO Customer Satisfaction"); }
    @FXML public void showEmployeePerformance(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOEmployeePerformanceView.fxml", "CEO Employee Performance"); }
    @FXML public void showReports(ActionEvent actionEvent)           { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/CEO/CEOReportsView.fxml", "CEO Reports"); }

    private void openWindow(ActionEvent actionEvent, String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            AlertHelper.error("Navigation Error", "Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
            