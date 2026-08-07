package application.controllers.productionmanager;

import application.controllers.DashboardBaseController;
import application.utilities.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProductionManagerDashboardController extends DashboardBaseController {

    @FXML public void showPlanning(ActionEvent actionEvent)   { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMProductionPlanningView.fxml", "PM Production Planning"); }
    @FXML public void showProgress(ActionEvent actionEvent)   { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMProductionProgressView.fxml", "PM Production Progress"); }
    @FXML public void showInventory(ActionEvent actionEvent)  { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMInventoryMonitoringView.fxml", "PM Inventory Monitoring"); }
    @FXML public void showStatus(ActionEvent actionEvent)     { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMProductionStatusView.fxml", "PM Production Status"); }
    @FXML public void showQuality(ActionEvent actionEvent)    { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMQualityInspectionView.fxml", "PM Quality Inspection"); }
    @FXML public void showReports(ActionEvent actionEvent)    { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMReportsView.fxml", "PM Reports"); }
    @FXML public void showStaffShift(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMStaffShiftView.fxml", "PM Staff Shift"); }
    @FXML public void showIncidents(ActionEvent actionEvent)  { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/Production Manager/PMIncidentManagementView.fxml", "PM Incident Management"); }

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
