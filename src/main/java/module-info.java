module jobportal {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mongo.java.driver;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires bcrypt;
    requires jbcrypt;

    opens com.jobportal.controllers to javafx.fxml;
    exports com.jobportal.controllers;
    opens com.jobportal.main to javafx.fxml;
    exports com.jobportal.main;
    exports com.jobportal.models;
}
