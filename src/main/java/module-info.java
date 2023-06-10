module tvz.java.rafaelprojekt.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires AnimateFX;
    requires java.sql;
    requires java.desktop;
    requires org.jsoup;
    requires org.slf4j;
    requires org.json;


    exports tvz.java.rafaelprojekt.entity;
    opens tvz.java.rafaelprojekt.entity to javafx.fxml;
    opens tvz.java.rafaelprojekt.main to javafx.fxml;
    exports tvz.java.rafaelprojekt.main;


}