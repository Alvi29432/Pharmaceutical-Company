module PharmaCompanyMS {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    // Open FXML-loaded packages to javafx.fxml
    opens application.controllers to javafx.fxml;
    opens application.controllers.customer to javafx.base, javafx.fxml;
    opens application.controllers.productionmanager to javafx.base, javafx.fxml;
    opens application.controllers.chiefexecutiveofficer to javafx.base, javafx.fxml;
    opens application.controllers.accountant to javafx.base, javafx.fxml;
    opens application.model.common to javafx.base;
    opens application.model.chiefexecutiveofficer to javafx.base;
    opens application.model.customer to javafx.base;
    opens application.model.productionmanager to javafx.base;
    opens application.model.accountant to javafx.base;
    opens application.users to javafx.base;

    exports application.main;
    exports application.controllers;
    exports application.model.common;
    exports application.model.chiefexecutiveofficer;
    exports application.model.customer;
    exports application.model.productionmanager;
    exports application.model.accountant;
    exports application.files;
    exports application.users;
}
