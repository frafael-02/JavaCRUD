package tvz.java.rafaelprojekt.main;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class MenuController {






    public void signOutClicked() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 547);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode);
        MainApplication.getMainStage().setScene(scene);
        MainApplication.getMainStage().show();
    }

    public void leaderboardsClicked() throws IOException, InterruptedException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("loading.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = new Stage();
        stage.setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        MainApplication.getMainStage().getScene().getWindow().hide();


    }

    public void championsClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("champions.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        MainApplication.getMainStage().setScene(scene);
        MainApplication.getMainStage().show();

    }

    public void searchClicked() throws IOException
    {
        LoginController.showScreen();
    }

    public void matchesClicked() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("matches.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        MainApplication.getMainStage().setScene(scene);
        MainApplication.getMainStage().show();

    }

    public void changesClicked() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("changes.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        MainApplication.getMainStage().setScene(scene);
        MainApplication.getMainStage().show();
    }







}
