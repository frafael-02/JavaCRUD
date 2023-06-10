package tvz.java.rafaelprojekt.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class LoadingController {
    public static Logger logger = LoggerFactory.getLogger(LoadingController.class);


    @FXML
    private StackPane rootPane;

public void initialize()
{
    new LoadingScreen().start();
}

    class LoadingScreen extends Thread{

        @Override
        public void run()
        {
            try{
                Thread.sleep(2000);
                Platform.runLater(new Runnable(){
                    @Override
                            public void run()
                    {
                        Parent root = null;
                        try{
                           root = FXMLLoader.load(MainApplication.class.getResource("leaderboards.fxml"));

                        }
                        catch(IOException e)
                        {
                            logger.debug("Error while loading leaderboards", e);
                            throw new RuntimeException("Error while loading leaderboards, please try again");
                        }
                        Scene scene = new Scene(root, 800, 600);
                        MainApplication.getMainStage().setScene(scene);
                        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
                        MainApplication.getMainStage().show();
                        rootPane.getScene().getWindow().hide();


                    }
                });

            } catch (InterruptedException e) {
                logger.debug(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
