package tvz.java.rafaelprojekt.main;

import animatefx.animation.Pulse;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.entity.Profile;

import java.io.IOException;
import java.util.Optional;

public class SearchController {

    private static Logger logger = LoggerFactory.getLogger(ProfileViewController.class);

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label label;

    @FXML
    private Text searchFailedText;

    @FXML
    private Text searchResultText;

    @FXML
    private ChoiceBox<String> serverChoiceBox;

    public void initialize()
    {


        usernameTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    enterPressed();
                }
            }
        });
        serverChoiceBox.getItems().add("EU");
        serverChoiceBox.getItems().add("NA");
        serverChoiceBox.getItems().add("LAM");


    }
    public void enterPressed()
    {
        StringBuffer stringBuffer = new StringBuffer();
        if(usernameTextField.getText().isEmpty())
        {
            stringBuffer.append("Enter an username\n");
            searchResultText.setVisible(false);
        }
        if(serverChoiceBox.getSelectionModel().isEmpty())
        {stringBuffer.append("Pick a server");
        searchResultText.setVisible(false);}

        if(!usernameTextField.getText().isEmpty() && !serverChoiceBox.getSelectionModel().isEmpty())
        {
            stringBuffer.setLength(0);

            Optional<Profile> result =
                    MainApplication.profileList.stream().filter(profile -> profile.getUsername().equals(usernameTextField.getText()) && profile.getServer().getCode().equals(serverChoiceBox.getSelectionModel().getSelectedItem())).findFirst();
            if(result.isPresent())
            {
                MainApplication.viewedProfile = result.get();
                try {
                    showFoundProfile();
                } catch (IOException e) {

                    logger.debug("Error while loading a profile.", e);
                    throw new RuntimeException("Error while loading a profile, servers down, try again later.");
                }
            }
            else
            {
                searchResultText.setVisible(true);
                searchResultText.setText("User not found!");}
        }

        searchFailedText.setText(stringBuffer.toString());
        searchFailedText.setVisible(true);
    }

    public void pressedOutside()
    {
        label.requestFocus();

    }

    public void showFoundProfile() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("profileView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        MainApplication.getMainStage().setScene(scene);
        MainApplication.getMainStage().show();
        new Pulse(scene.getRoot()).play();
    }





}
