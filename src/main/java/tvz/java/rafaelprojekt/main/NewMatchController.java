package tvz.java.rafaelprojekt.main;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.*;
import tvz.java.rafaelprojekt.exceptions.WinnerDecisionException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



public class NewMatchController {
    @FXML
    private ChoiceBox<String> serverChoiceBox;
    @FXML
    private ChoiceBox<Profile> redSidePlayerChoiceBox;
    @FXML
    private ChoiceBox<Profile> blueSidePlayerChoiceBox;
    @FXML
    private ChoiceBox<Champion> redSideChampionChoiceBox;
    @FXML
    private ChoiceBox<Champion> blueSideChampionChoiceBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField redKillsTextField;
    @FXML
    private TextField bluekillsTextField;
    @FXML
    private ChoiceBox<Profile> winnerChoiceBox;
    @FXML
    private Text statusText;
    private List<Champion> championNames;
    public static Logger logger = LoggerFactory.getLogger(NewMatchController.class);


    public void initialize()
    {
        serverChoiceBox.getItems().addAll("EU", "NA", "LAM");
        serverChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                serverPicked();
            }
        });

        redSidePlayerChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!redSidePlayerChoiceBox.getSelectionModel().isEmpty())
                    redSideChampionChoiceBox.setDisable(false);
            }
        });

       blueSidePlayerChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!blueSidePlayerChoiceBox.getSelectionModel().isEmpty())
                    blueSideChampionChoiceBox.setDisable(false);
            }
        });

        redSideChampionChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!redSideChampionChoiceBox.getSelectionModel().isEmpty())
                    redKillsTextField.setDisable(false);
            }
        });
        blueSideChampionChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!blueSideChampionChoiceBox.getSelectionModel().isEmpty())
                    bluekillsTextField.setDisable(false);
            }
        });

        redKillsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                enteredKills();
            }
            catch (WinnerDecisionException e)
            {
                logger.debug(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Cannot decide the winner");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        bluekillsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                enteredKills();
            }
            catch (WinnerDecisionException e)
            {
                logger.debug(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Cannot decide the winner");
                alert.setContentText(e.getMessage());
                alert.showAndWait();

            }
        });





        championNames = MainApplication.championList;

    }

    public void serverPicked()
    {
        redSidePlayerChoiceBox.setDisable(false);
        blueSidePlayerChoiceBox.setDisable(false);
        redSidePlayerChoiceBox.setItems(FXCollections.observableList(ProfileGetter.getByServer(serverChoiceBox.getSelectionModel().getSelectedItem())));
        blueSidePlayerChoiceBox.setItems(FXCollections.observableList(ProfileGetter.getByServer(serverChoiceBox.getSelectionModel().getSelectedItem())));
        if(redSideChampionChoiceBox.getItems().isEmpty() && blueSideChampionChoiceBox.getItems().isEmpty())
        {
            redSideChampionChoiceBox.setItems(FXCollections.observableList(championNames));
            blueSideChampionChoiceBox.setItems(FXCollections.observableList(championNames));
        }

    }

    public void enteredKills() throws WinnerDecisionException
    {
        winnerChoiceBox.getItems().clear();
        try{
            if(!blueSidePlayerChoiceBox.getSelectionModel().isEmpty() && !redSidePlayerChoiceBox.getSelectionModel().isEmpty())
            {
                if(!bluekillsTextField.getText().isEmpty() && !redKillsTextField.getText().isEmpty())
                {
                    winnerChoiceBox.setDisable(false);
                    if(Integer.parseInt(bluekillsTextField.getText()) > Integer.parseInt(redKillsTextField.getText()))
                    {

                        winnerChoiceBox.getItems().add(blueSidePlayerChoiceBox.getSelectionModel().getSelectedItem());
                        winnerChoiceBox.getSelectionModel().select(blueSidePlayerChoiceBox.getSelectionModel().getSelectedItem());

                    }
                    else if(Integer.parseInt(bluekillsTextField.getText()) < Integer.parseInt(redKillsTextField.getText()))
                    {
                        winnerChoiceBox.getItems().add(redSidePlayerChoiceBox.getSelectionModel().getSelectedItem());
                        winnerChoiceBox.getSelectionModel().select(redSidePlayerChoiceBox.getSelectionModel().getSelectedItem());

                    }
                    else{
                        winnerChoiceBox.getItems().add(blueSidePlayerChoiceBox.getSelectionModel().getSelectedItem());
                        winnerChoiceBox.getItems().add(redSidePlayerChoiceBox.getSelectionModel().getSelectedItem());

                    }
                }

            }
        }
        catch (NumberFormatException e)
        {
            throw new WinnerDecisionException("Entered kills are invalid.");
        }


    }

    public void addClicked()
    {
        StringBuffer stringBuffer = new StringBuffer();
        if(serverChoiceBox.getSelectionModel().isEmpty())
            stringBuffer.append("Select a server first");
        else{

            if(redSidePlayerChoiceBox.getSelectionModel().isEmpty() || blueSidePlayerChoiceBox.getSelectionModel().isEmpty())
            {
                if(blueSidePlayerChoiceBox.getSelectionModel().isEmpty())
                    stringBuffer.append("Choose blue side player.\n");
                if(redSidePlayerChoiceBox.getSelectionModel().isEmpty())
                    stringBuffer.append("Choose red side player.\n");
            }
            else if(blueSidePlayerChoiceBox.getSelectionModel().getSelectedItem().equals(redSidePlayerChoiceBox.getSelectionModel().getSelectedItem()))
                stringBuffer.append("Can't choose the same player twice.\n");
            if(redSideChampionChoiceBox.getSelectionModel().isEmpty() || blueSideChampionChoiceBox.getSelectionModel().isEmpty())
            {
                if(blueSideChampionChoiceBox.getSelectionModel().isEmpty())
                    stringBuffer.append("Choose blue side champion.\n");
                if(redSideChampionChoiceBox.getSelectionModel().isEmpty())
                    stringBuffer.append("Choose red side champion.\n");
            }
            else if(redSideChampionChoiceBox.getSelectionModel().getSelectedItem().equals(blueSideChampionChoiceBox.getSelectionModel().getSelectedItem()))
                stringBuffer.append("Can't choose the same champion twice.\n");


            if(redKillsTextField.getText().isEmpty())
                stringBuffer.append("Enter kills for red side.\n");
            if(bluekillsTextField.getText().isEmpty())
                stringBuffer.append("Enter kills for blue side.\n");
            if(winnerChoiceBox.getSelectionModel().isEmpty())
                stringBuffer.append("Select a winner.\n");
            if(datePicker.getValue()==null)
                stringBuffer.append("Pick a date.\n");
            if(datePicker.getValue().isAfter(LocalDate.now()))
                stringBuffer.append("Date cannot be in the future.");

        }




        statusText.setText(stringBuffer.toString());

        if(statusText.getText().isEmpty())
            addMatch();

    }

    public void addMatch()
    {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Please confirm.");
        alert.setContentText("Are you sure you want to add the match?");
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK))
        {
            try{
                Long redId = redSidePlayerChoiceBox.getSelectionModel().getSelectedItem().getId();
                Long blueId = blueSidePlayerChoiceBox.getSelectionModel().getSelectedItem().getId();
                Long redChampId = redSideChampionChoiceBox.getSelectionModel().getSelectedItem().getId();
                Long blueChampId = blueSideChampionChoiceBox.getSelectionModel().getSelectedItem().getId();
                Long winnerId = winnerChoiceBox.getSelectionModel().getSelectedItem().getId();
                LocalDate localDate = datePicker.getValue();
                int redKills = Integer.parseInt(redKillsTextField.getText());
                int blueKills = Integer.parseInt(bluekillsTextField.getText());
                Champion redChampion = Champion.getById(redChampId);
                Champion blueChampion = Champion.getById(blueChampId);
                Match match = new Match(0L, redId, blueId, winnerId, redKills, blueKills,redChampion, blueChampion, localDate);
                DatabaseQueries.addNewMatch(match);
                ChangedEntity<Match> changedEntity = new ChangedEntity<>(null, match, LocalDateTime.now());
                MainApplication.changesList.add(changedEntity.findChanges());
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setHeaderText("New match added.");
                alert1.setContentText("Match added successfully.");
                alert1.showAndWait();
            }

            catch (IllegalAccessException e) {
                logger.debug("Error while saving the change to the list", e);
            }
            catch(NumberFormatException e)
            {
                logger.debug("Letter input as a number", e);
                Alert alert1 = new Alert(Alert.AlertType.WARNING);
                alert1.setHeaderText("Cannot add the match.");
                alert1.setContentText("Recheck all input numbers");
            }
        }


    }

    public void resetClicked()
    {
        serverChoiceBox.getSelectionModel().clearSelection();
        redSideChampionChoiceBox.setDisable(true);
        redSidePlayerChoiceBox.setDisable(true);
       blueSideChampionChoiceBox.setDisable(true);
       blueSidePlayerChoiceBox.setDisable(true);
       datePicker.setValue(null);
        redKillsTextField.clear();
        bluekillsTextField.clear();
        redKillsTextField.setDisable(true);
        bluekillsTextField.setDisable(true);

    }

    public void goBackClicked() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("matches.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        MainApplication.getMainStage().setScene(scene);
        MainApplication.getMainStage().show();
    }

}
