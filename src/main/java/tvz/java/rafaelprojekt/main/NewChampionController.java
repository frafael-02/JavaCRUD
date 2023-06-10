package tvz.java.rafaelprojekt.main;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.Champion;
import tvz.java.rafaelprojekt.entity.ChangedEntity;

import java.time.LocalDateTime;


public class NewChampionController {
    @FXML
    private TextField usernameTextField;

    public static Logger logger = LoggerFactory.getLogger(NewChampionController.class);

    @FXML
    private AnchorPane rootPane;

    public void closeClicked()
    {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
        MainApplication.getMainStage().show();

    }

    public void addClicked() {
        if (!usernameTextField.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Please confirm.");
            alert.setContentText("Are you sure you want to add a new champion?");
            alert.showAndWait();
            if(alert.getResult().equals(ButtonType.OK))
            {
                if(!Champion.nameUsed(usernameTextField.getText()))
                {
                    DatabaseQueries.addChampion(usernameTextField.getText());
                    ChampionsController.tableviewstatic.setItems(FXCollections.observableList(MainApplication.championList));
                    ChangedEntity<Champion> changedEntity = new ChangedEntity<>(null, new Champion(0L, usernameTextField.getText()), LocalDateTime.now());
                    try {
                        MainApplication.changesList.add(changedEntity.findChanges());
                    } catch (IllegalAccessException e) {
                        logger.debug("Error while trying to add the change to the list, e");

                    }
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setHeaderText("New champion added.");
                    alert1.setContentText("Champion added successfully.");
                    alert1.showAndWait();
                }
                else{
                    Alert alert1 = new Alert(Alert.AlertType.WARNING);
                    alert1.setHeaderText("Cannot add the champion.");
                    alert1.setContentText("Name taken.");
                    alert1.showAndWait();
                }


            }


        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot add the champion");
            alert.setContentText("Fill in the name.");
            alert.showAndWait();
        }
    }


}
