package application.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertHelper {

    private AlertHelper() {
    }

    public static void info(String title, String message) {
        show(AlertType.INFORMATION, title, message);
    }

    public static void warn(String title, String message) {
        show(AlertType.WARNING, title, message);
    }

    public static void error(String title, String message) {
        show(AlertType.ERROR, title, message);
    }

    private static void show(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
