package application.main;

import application.users.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Seed default/sample data into the .bin files if they are empty.
            DefaultUserSeeder.seedIfEmpty();
            CEODataSeeder.seedIfEmpty();
            CustomerDataSeeder.seedIfEmpty();
            ProductionManagerDataSeeder.seedIfEmpty();
            AccountantDataSeeder.seedIfEmpty();

            // Hard-coded scene switch: load LoginView.fxml and show it on the primary stage.
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/fxml/common/LoginView.fxml"));
                Parent root = fxmlLoader.load();
                Scene loginScene = new Scene(root);

                primaryStage.setTitle("Pharma Company MS - Login");
                primaryStage.setScene(loginScene);
                primaryStage.setResizable(false);
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
