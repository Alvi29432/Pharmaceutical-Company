package application.controllers.accountant;

import application.controllers.DashboardBaseController;
import application.utilities.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AccountantDashboardController extends DashboardBaseController {

    @FXML public void showRevenue(ActionEvent actionEvent)            { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantRevenueView.fxml", "Accountant Revenue"); }
    @FXML public void showExpenses(ActionEvent actionEvent)           { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantExpensesView.fxml", "Accountant Expenses"); }
    @FXML public void showFinancialReports(ActionEvent actionEvent)   { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantFinancialReportsView.fxml", "Accountant Financial Reports"); }
    @FXML public void showCashFlow(ActionEvent actionEvent)           { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantCashFlowView.fxml", "Accountant Cash Flow"); }
    @FXML public void showAccountsPayable(ActionEvent actionEvent)    { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantAccountsPayableView.fxml", "Accounts Payable"); }
    @FXML public void showAccountsReceivable(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantAccountsReceivableView.fxml", "Accounts Receivable"); }
    @FXML public void showProductionCost(ActionEvent actionEvent)     { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantProductionCostView.fxml", "Production Cost"); }
    @FXML public void showCompliance(ActionEvent actionEvent)         { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantComplianceView.fxml", "Compliance"); }
    @FXML public void showBudgetPlanning(ActionEvent actionEvent)     { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantBudgetPlanningView.fxml", "Budget Planning"); }
    @FXML public void showFinancialDashboard(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Abida Mamun Tanha/FXML Files/accountant/AccountantFinancialDashboardView.fxml", "Financial Dashboard"); }

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
