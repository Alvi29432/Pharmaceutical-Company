package application.controllers.customer;

import application.controllers.DashboardBaseController;
import application.utilities.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomerDashboardController extends DashboardBaseController {

    @FXML public void showProfile(ActionEvent actionEvent)        { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerProfileView.fxml", "Customer Profile"); }
    @FXML public void showCreateAccount(ActionEvent actionEvent)  { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerCreateAccountView.fxml", "Create Account"); }
    @FXML public void showMedicineSearch(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerMedicineSearchView.fxml", "Search Medicines"); }
    @FXML public void showPlaceOrder(ActionEvent actionEvent)     { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerPlaceOrderView.fxml", "Place Order"); }
    @FXML public void showTrackOrder(ActionEvent actionEvent)     { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerTrackOrderView.fxml", "Track Order"); }
    @FXML public void showPurchaseHistory(ActionEvent actionEvent){ openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerPurchaseHistoryView.fxml", "Purchase History"); }
    @FXML public void showInvoices(ActionEvent actionEvent)       { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerInvoicesView.fxml", "Invoices"); }
    @FXML public void showReviews(ActionEvent actionEvent)        { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerReviewsView.fxml", "Reviews"); }
    @FXML public void showFeedback(ActionEvent actionEvent)       { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerFeedbackView.fxml", "Feedback"); }
    @FXML public void showSupportTickets(ActionEvent actionEvent) { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerSupportTicketsView.fxml", "Support Tickets"); }
    @FXML public void showNotifications(ActionEvent actionEvent)  { openWindow(actionEvent, "/application/fxml/Tariquzzaman Alvi/FXML Files/customer/CustomerNotificationsView.fxml", "Notifications"); }

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
