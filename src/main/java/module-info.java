module com.oscars.vehiclemaintenancesystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.persistence; // If you're using a database
    requires java.naming; // If you're using a database
    requires javafx.graphics;
    requires jakarta.persistence;
    requires antlr; // If you're using a database




    opens com.oscars.vehiclemaintenancesystem to javafx.fxml;
    opens com.oscars.vehiclemaintenancesystem.model to org.hibernate.orm.core;


    opens com.oscars.vehiclemaintenancesystem.controller to javafx.fxml;

    exports com.oscars.vehiclemaintenancesystem;
    exports com.oscars.vehiclemaintenancesystem.controller;
    exports com.oscars.vehiclemaintenancesystem.model;
    exports com.oscars.vehiclemaintenancesystem.service;

}
