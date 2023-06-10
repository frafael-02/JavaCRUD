package tvz.java.rafaelprojekt.main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.Data;
import tvz.java.rafaelprojekt.data.USER_MODE;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.*;
import tvz.java.rafaelprojekt.exceptions.LoginDataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class MainApplication extends Application {
    public static Stage mainStage;

    public static USER_MODE loginMode = USER_MODE.NOT_LOGGED_IN;

    public static String loggedUser = "None";

    public static Profile viewedProfile;

    public static Map<String, String> loginData;
    public static Logger logger = LoggerFactory.getLogger(MainApplication.class);

    static {
        try {
            loginData = Data.getLoginProfiles();
        } catch (LoginDataException e) {
            logger.debug(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<Champion> championList = DatabaseQueries.getChampions();

    public static List<Match> matchList = DatabaseQueries.getAllMatches();

    public static List<Rank> rankList = DatabaseQueries.getRanks();
    public static List<Profile> profileList = DatabaseQueries.getProfiles();

    public static List<String> oldChanges = ChangedEntity.readChanges();
    public static List<String> changesList = new ArrayList<>(oldChanges);





    @Override
    public void start(Stage stage) throws IOException {
        mainStage=stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 547 );
        stage.setTitle("User mode: " + loginMode);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setScene(scene);
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("images/logo.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainStage()
    {
        return mainStage;
    }
}