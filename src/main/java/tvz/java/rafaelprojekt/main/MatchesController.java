package tvz.java.rafaelprojekt.main;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.USER_MODE;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MatchesController {
    @FXML
    private ChoiceBox<Champion> redSideChampion;
    @FXML
    private ChoiceBox<Champion> blueSideChampion;
    @FXML
    private ChoiceBox<Profile> redSidePlayer;
    @FXML
    private  ChoiceBox<Profile> blueSidePlayer;
    @FXML
    private ChoiceBox<Champion> anySideChampion;
    @FXML
    private ChoiceBox<String> serverChoiceBox;
    @FXML
    private TextField totalKillsField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton equalsButton;
    @FXML
    private RadioButton greaterButton;
    @FXML
    private RadioButton lessButton;
    @FXML
    private TableView<Match> matchTableView;
    @FXML
    private TableColumn<Match, String> redSideColumn;
    @FXML
    private TableColumn<Match, String> blueSideColumn;
    @FXML
    private TableColumn<Match, String> winnerColumn;
    @FXML
    private TableColumn<Match, String> redChampionColumn;
    @FXML
    private TableColumn<Match, String> blueChampionColumn;
    @FXML
    private TableColumn<Match, String> redKillsColumn;
    @FXML
    private TableColumn<Match, String> blueKillsColumn;
    @FXML
    private TableColumn<Match, String> dateColumn;
    @FXML
    private TableColumn<Match, String> serverColumn;
    private List<Match> matchList;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane editPane;
    @FXML
    private Button editButton;

    @FXML
    private ChoiceBox<Profile> editRedProfile;

    @FXML
    private ChoiceBox<Profile> editBlueProfile;
    @FXML
    private ChoiceBox<Champion> editRedChampion;

    @FXML
    private ChoiceBox<Champion> editBlueChampion;
    @FXML
    private TextField editRedKills;
    @FXML
    private TextField editBlueKills;
    @FXML
    private DatePicker editDatePicker;
    @FXML
    private ChoiceBox<String> editServer;
    @FXML
    private ChoiceBox<Profile> editWinner;

    @FXML
    private Button addButton;

    public static Logger logger = LoggerFactory.getLogger(MatchesController.class);

   private ToggleGroup toggleGroup;





    public void initialize()
    {
       toggleGroup = new ToggleGroup();
       equalsButton.setToggleGroup(toggleGroup);
       greaterButton.setToggleGroup(toggleGroup);
       lessButton.setToggleGroup(toggleGroup);


        anySideChampion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                championsChecking();
            }
        });
        blueSideChampion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                championsChecking();
            }
        });
        redSideChampion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                championsChecking();
            }
        });
       blueSidePlayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playerChecking();
            }
        });
       redSidePlayer.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent actionEvent) {
               playerChecking();
           }
       });

       matchTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
           if (newSelection != null) {
               editButton.setDisable(false);
           }
           if(newSelection == null)
           {
               editButton.setDisable(true);
           }
       });

        editServer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != oldSelection) {
                editServerChanged(newSelection);
            }
        });

        editRedProfile.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection == null || oldSelection != newSelection) {
                editPlayersChanged();
            }
        });
        editBlueProfile.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection == null || oldSelection != newSelection) {
                editPlayersChanged();
            }
        });

        serverChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            if(oldSelection == null)
            {
                serverChoiceSet(newSelection);
            }

             });

        redSideChampion.getItems().addAll(MainApplication.championList);
        blueSideChampion.getItems().addAll(MainApplication.championList);
        anySideChampion.getItems().addAll(MainApplication.championList);
        redSidePlayer.getItems().addAll(MainApplication.profileList);
        blueSidePlayer.getItems().addAll(MainApplication.profileList);
        for(Server s : Server.values())
        {
            serverChoiceBox.getItems().addAll(s.getCode());
        }
        redSideColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRedSide().getUsername()));
        blueSideColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBlueSide().getUsername()));
        winnerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWinner().getUsername()));
        redChampionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRedChampion().getName()));
        blueChampionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBlueChampion().getName()));
        redKillsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRedKills())));
        blueKillsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBlueKills())));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMatchDate().format(formatter)));
        serverColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBlueSide().getServer().getCode()));
        matchList = MainApplication.matchList;
        matchTableView.setItems(FXCollections.observableList(matchList));

        if(MainApplication.loginMode.equals(USER_MODE.GUEST))
            addButton.setDisable(true);
    }


    public void serverChoiceSet(String newChoice)
    {
        if(blueSidePlayer.getSelectionModel().isEmpty() && redSidePlayer.getSelectionModel().isEmpty())
        {
            blueSidePlayer.setItems(FXCollections.observableList(ProfileGetter.getByServer(newChoice)));
            redSidePlayer.setItems(FXCollections.observableList(ProfileGetter.getByServer(newChoice)));

        }
    }

    private void playerChecking()
    {
        if(!blueSidePlayer.getSelectionModel().isEmpty())
        {
            if(redSidePlayer.getSelectionModel().isEmpty())
            {



                Profile selectedProfile = blueSidePlayer.getSelectionModel().getSelectedItem();
                String server = selectedProfile.getServer().getCode();
                List<Profile> profileList = new ArrayList<>(ProfileGetter.getByServer(server));
                blueSidePlayer.setItems(FXCollections.observableList(profileList));
                profileList= profileList.stream().filter(names -> (!names.getId().equals(selectedProfile.getId()))).toList();
                redSidePlayer.setItems(FXCollections.observableList(profileList));
                blueSidePlayer.getSelectionModel().select(selectedProfile);
                if(serverChoiceBox.getSelectionModel().isEmpty())
                {
                    serverChoiceBox.getSelectionModel().select(server);
                    serverChoiceBox.setDisable(true);
                }
                else
                    serverChoiceBox.setDisable(true);


            }
            else
            {
                Profile previousSelectedItem = redSidePlayer.getSelectionModel().getSelectedItem();
                Profile selectedItem = blueSidePlayer.getSelectionModel().getSelectedItem();
                String server = selectedItem.getServer().getCode();
                List<Profile> profileList = new ArrayList<>(ProfileGetter.getByServer(server));
                blueSidePlayer.setItems(FXCollections.observableList(ProfileGetter.getByServer(server).stream().filter(name -> !name.getId().equals(previousSelectedItem.getId())).toList()));
                profileList= profileList.stream().filter(names -> (!names.getId().equals(selectedItem.getId()))).toList();
                redSidePlayer.setItems(FXCollections.observableList(profileList));
                blueSidePlayer.getSelectionModel().select(selectedItem);
               redSidePlayer.getSelectionModel().select(previousSelectedItem);
               if(serverChoiceBox.getSelectionModel().isEmpty())
               {
                   serverChoiceBox.getSelectionModel().select(server);
                   serverChoiceBox.setDisable(true);
               }

            }

        }
        if(!redSidePlayer.getSelectionModel().isEmpty())
        {
            if(blueSidePlayer.getSelectionModel().isEmpty())
            {
                Profile selectedItem = redSidePlayer.getSelectionModel().getSelectedItem();
                Profile profile = MainApplication.profileList.stream().filter(player -> player.equals(selectedItem)).findFirst().get();
                String server = profile.getServer().getCode();
                List<Profile> profileList = new ArrayList<>(ProfileGetter.getByServer(server));
                redSidePlayer.setItems(FXCollections.observableList(profileList));
                profileList= profileList.stream().filter(names -> (!names.getId().equals(selectedItem.getId()))).toList();
                blueSidePlayer.setItems(FXCollections.observableList(profileList));
                redSidePlayer.getSelectionModel().select(selectedItem);
                if(serverChoiceBox.getSelectionModel().isEmpty())
                {
                    serverChoiceBox.getSelectionModel().select(server);
                    serverChoiceBox.setDisable(true);
                }
                else serverChoiceBox.setDisable(true);

            }
            else{
                Profile previousSelectedItem = blueSidePlayer.getSelectionModel().getSelectedItem();
                Profile selectedItem = redSidePlayer.getSelectionModel().getSelectedItem();
                String server = selectedItem.getServer().getCode();
                List<Profile> profileList = new ArrayList<>(ProfileGetter.getByServer(server));
                redSidePlayer.setItems(FXCollections.observableList(ProfileGetter.getByServer(server).stream().filter(name -> !name.getId().equals(previousSelectedItem.getId())).toList()));
                profileList= profileList.stream().filter(names -> (!names.getId().equals(selectedItem.getId()))).toList();
                redSidePlayer.getSelectionModel().select(selectedItem);
                blueSidePlayer.setItems(FXCollections.observableList(profileList));
                blueSidePlayer.getSelectionModel().select(previousSelectedItem);
                if(serverChoiceBox.getSelectionModel().isEmpty())
                {
                    serverChoiceBox.getSelectionModel().select(server);
                    serverChoiceBox.setDisable(true);
                }

            }
        }
    }

    private void championsChecking()
    {



        if(!anySideChampion.getSelectionModel().isEmpty())
        {
            blueSideChampion.getSelectionModel().clearSelection();
            blueSideChampion.setDisable(true);
            redSideChampion.getSelectionModel().clearSelection();
            redSideChampion.setDisable(true);
        }
        else
        {


                if(!redSideChampion.getSelectionModel().isEmpty())
                    {
                        if(blueSideChampion.getSelectionModel().isEmpty())
                        {
                            Champion selectedItem = redSideChampion.getSelectionModel().getSelectedItem();
                            List<Champion> tempList = new ArrayList<>(MainApplication.championList);
                            tempList= tempList.stream().filter(names -> (!names.equals(selectedItem))).toList();
                            blueSideChampion.setItems(FXCollections.observableList(tempList));
                        }
                        else{
                            Champion previousSelectedItem = blueSideChampion.getSelectionModel().getSelectedItem();
                            Champion selectedItem = redSideChampion.getSelectionModel().getSelectedItem();
                            List<Champion> tempList = new ArrayList<>(MainApplication.championList);
                            tempList= tempList.stream().filter(names -> (!names.equals(selectedItem))).toList();
                            blueSideChampion.setItems(FXCollections.observableList(tempList));
                            blueSideChampion.getSelectionModel().select(previousSelectedItem);
                        }


                    }

                if(!blueSideChampion.getSelectionModel().isEmpty())
                    {
                        if(redSideChampion.getSelectionModel().isEmpty())
                        {
                            Champion selectedItem = blueSideChampion.getSelectionModel().getSelectedItem();
                            List<Champion> tempList = new ArrayList<>(MainApplication.championList);
                            tempList= tempList.stream().filter(names -> (!names.equals(selectedItem))).toList();
                            redSideChampion.setItems(FXCollections.observableList(tempList));
                        }
                        else{
                            Champion previousSelectedItem = redSideChampion.getSelectionModel().getSelectedItem();
                            Champion selectedItem = blueSideChampion.getSelectionModel().getSelectedItem();
                            List<Champion> tempList = new ArrayList<>(MainApplication.championList);
                            tempList= tempList.stream().filter(names -> (!names.equals(selectedItem))).toList();
                            redSideChampion.setItems(FXCollections.observableList(tempList));
                            redSideChampion.getSelectionModel().select(previousSelectedItem);

                        }


                    }
        }
    }

    public void resetClicked()
    {
        editButton.setDisable(true);
        serverChoiceBox.setDisable(false);
        redSideChampion.setDisable(false);
        blueSideChampion.setDisable(false);
        redSideChampion.getSelectionModel().clearSelection();
        blueSideChampion.getSelectionModel().clearSelection();
        anySideChampion.getSelectionModel().clearSelection();
        blueSidePlayer.getSelectionModel().clearSelection();
        redSidePlayer.getSelectionModel().clearSelection();
        serverChoiceBox.getSelectionModel().clearSelection();
        redSideChampion.setItems(FXCollections.observableList(MainApplication.championList));
        blueSideChampion.setItems(FXCollections.observableList(MainApplication.championList));
        redSidePlayer.setItems(FXCollections.observableList(MainApplication.profileList));
        blueSidePlayer.setItems(FXCollections.observableList(MainApplication.profileList));
        totalKillsField.clear();
        datePicker.setValue(null);
        matchTableView.setItems(FXCollections.observableList(matchList));
        matchTableView.getSelectionModel().clearSelection();
    }

    public void filterClicked()
    {
        List<Match> filteredList = new ArrayList<>(matchList);
        try{
            if(!anySideChampion.getSelectionModel().isEmpty())
            {
            filteredList=filteredList.stream().filter(match -> match.getBlueChampion().equals(anySideChampion.getSelectionModel().getSelectedItem()) || match.getRedChampion().equals(anySideChampion.getSelectionModel().getSelectedItem())).toList();
            }

        else {
            if(!redSideChampion.getSelectionModel().isEmpty())
            {
                filteredList=filteredList.stream().filter(match -> match.getRedChampion().equals(redSideChampion.getSelectionModel().getSelectedItem())).toList();
            }
            if(!blueSideChampion.getSelectionModel().isEmpty())
            {
                filteredList=filteredList.stream().filter(match -> match.getBlueChampion().equals(blueSideChampion.getSelectionModel().getSelectedItem())).toList();

            }

        }
            if(!serverChoiceBox.getSelectionModel().isEmpty())
                filteredList=filteredList.stream().filter(match -> match.getRedSide().getServer().getCode().equals(serverChoiceBox.getSelectionModel().getSelectedItem())).toList();

            if(!redSidePlayer.getSelectionModel().isEmpty())
            {
                filteredList=filteredList.stream().filter(match -> match.getRedSide().equals(redSidePlayer.getSelectionModel().getSelectedItem())).toList();
            }
            if(!blueSidePlayer.getSelectionModel().isEmpty())
            {
                filteredList=filteredList.stream().filter(match -> match.getBlueSide().equals(blueSidePlayer.getSelectionModel().getSelectedItem())).toList();
            }
            if(!totalKillsField.getText().isEmpty())
            {
                if(equalsButton.isSelected())
                {
                    filteredList=filteredList.stream().filter(match -> {
                        int total=0;
                        total= match.getBlueKills()+match.getRedKills();
                        if(total == Integer.parseInt(totalKillsField.getText()))
                            return true;
                        return false;
                    }).toList();
                }
                else if(greaterButton.isSelected())
                {
                    filteredList=filteredList.stream().filter(match -> {
                        int total=0;
                        total= match.getBlueKills()+match.getRedKills();
                        if(total > Integer.parseInt(totalKillsField.getText()))
                            return true;
                        return false;
                    }).toList();
                }
                else if(lessButton.isSelected())
                {
                    filteredList=filteredList.stream().filter(match -> {
                        int total=0;
                        total= match.getBlueKills()+match.getRedKills();
                        if(total < Integer.parseInt(totalKillsField.getText()))
                            return true;
                        return false;
                    }).toList();
                }

            }

            if(datePicker.getValue() != null)
            {
                filteredList=filteredList.stream().filter(match -> match.getMatchDate().equals(datePicker.getValue())).toList();
            }


            matchTableView.setItems(FXCollections.observableList(filteredList));
        }
        catch (NumberFormatException e)
        {
            logger.debug(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Recheck entered numbers.");
            alert.setHeaderText("Invalid number input.");
            alert.showAndWait();
        }



    }

public void addNewClicked() throws IOException
{
    FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("newMatch.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 392);
    MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
    MainApplication.getMainStage().setScene(scene);
    MainApplication.getMainStage().show();
}

    public void deleteClicked()
    {
    if(!matchTableView.getSelectionModel().isEmpty())
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Please confirm.");
        alert.setContentText("Are you sure you want to delete the match?");
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.OK))
        {
            ChangedEntity<Match> changedEntity = new ChangedEntity<>(matchTableView.getSelectionModel().getSelectedItem(), null, LocalDateTime.now());
            try {
                MainApplication.changesList.add(changedEntity.findChanges());
            } catch (IllegalAccessException e) {
                logger.debug("Error while trying to save the change to the list", e);
            }
            DatabaseQueries.deleteMatch(matchTableView.getSelectionModel().getSelectedItem());
            MainApplication.matchList = DatabaseQueries.getAllMatches();
            matchList=MainApplication.matchList;
            int index = matchTableView.getSelectionModel().getSelectedIndex();

            for(Profile p:MainApplication.profileList)
            {
                if(p.getId().equals(matchTableView.getSelectionModel().getSelectedItem().getRedSideId()) || p.getId().equals(matchTableView.getSelectionModel().getSelectedItem().getBlueSideId()))
                    p.getMatchSet().remove(matchTableView.getSelectionModel().getSelectedItem());
            }
            matchTableView.getSelectionModel().clearSelection();
            List<Match> tempList = new ArrayList<>(matchTableView.getItems());
            tempList.remove(index);
            matchTableView.setItems(FXCollections.observableList(tempList));
            Alert alert1=new Alert(Alert.AlertType.INFORMATION);
            alert1.setHeaderText("Match deleted");
            alert1.setContentText("Match deleted successfully.");
            alert1.showAndWait();
        }

        }
    else{
        Alert alert=new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Cannot delete the match");
        alert.setContentText("No match was selected.");
        alert.showAndWait();

    }
        }

        public void editClicked()
        {
            if(!matchTableView.getSelectionModel().isEmpty())
            {
                mainPane.setDisable(true);
                editPane.setVisible(true);
                String code = matchTableView.getSelectionModel().getSelectedItem().getRedSide().getServer().getCode();
                List<String> serverList = new ArrayList<>(Arrays.asList("EU", "NA", "LAM"));
                editServer.setItems(FXCollections.observableList(serverList));
                editServer.getSelectionModel().select(code);
                List<Profile> editProfileList = new ArrayList<>(ProfileGetter.getByServer(code));
                editBlueProfile.setItems(FXCollections.observableList(editProfileList));
                editRedProfile.setItems(FXCollections.observableList(editProfileList));
                editBlueProfile.getSelectionModel().select(matchTableView.getSelectionModel().getSelectedItem().getBlueSide());
                editRedProfile.getSelectionModel().select(matchTableView.getSelectionModel().getSelectedItem().getRedSide());
                editRedChampion.setItems(FXCollections.observableList(MainApplication.championList));
                editBlueChampion.setItems(FXCollections.observableList(MainApplication.championList));
                editRedChampion.getSelectionModel().select(matchTableView.getSelectionModel().getSelectedItem().getRedChampion());
                editBlueChampion.getSelectionModel().select(matchTableView.getSelectionModel().getSelectedItem().getBlueChampion());
                editBlueKills.setText(String.valueOf(matchTableView.getSelectionModel().getSelectedItem().getBlueKills()));
                editRedKills.setText(String.valueOf(matchTableView.getSelectionModel().getSelectedItem().getRedKills()));
                editWinner.getSelectionModel().select(matchTableView.getSelectionModel().getSelectedItem().getWinner());
                editDatePicker.setValue(matchTableView.getSelectionModel().getSelectedItem().getMatchDate());
            }
            else{
                Alert alert=new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Cannot edit the match");
                alert.setContentText("No match was selected.");
                alert.showAndWait();
            }


        }

        public void cancelClicked()
        {
            mainPane.setDisable(false);
            editPane.setVisible(false);
        }

        public void editServerChanged(String code)
        {
            List<Profile> editProfileList = new ArrayList<>(ProfileGetter.getByServer(code));
            editBlueProfile.setItems(FXCollections.observableList(editProfileList));
            editRedProfile.setItems(FXCollections.observableList(editProfileList));


        }

        public void editPlayersChanged()
        {
            int index=-1;
            if(!editWinner.getSelectionModel().isEmpty())
                index = editWinner.getSelectionModel().getSelectedIndex();
            List<Profile> profileList2 = Arrays.asList(editBlueProfile.getSelectionModel().getSelectedItem(), editRedProfile.getSelectionModel().getSelectedItem());
            editWinner.setItems(FXCollections.observableList(profileList2));
            if(index != -1)
                editWinner.getSelectionModel().select(index);
        }

        public void editSaveClicked()
        {
            try{
                Integer redKills = null;
                Integer blueKills = null;
                Long id = matchTableView.getSelectionModel().getSelectedItem().getId();
                Long redPlayer = editRedProfile.getSelectionModel().getSelectedItem().getId();
                Long bluePlayer = editBlueProfile.getSelectionModel().getSelectedItem().getId();
                Champion redChamp = editRedChampion.getSelectionModel().getSelectedItem();
                Champion blueChamp = editBlueChampion.getSelectionModel().getSelectedItem();
                LocalDate date = editDatePicker.getValue();
                if(!redPlayer.equals(bluePlayer))
                {
                    if(!redChamp.equals(blueChamp))
                    {
                        if(!date.isAfter(LocalDate.now()))
                        {
                            if(!editRedKills.getText().isEmpty() && !editBlueKills.getText().isEmpty())
                            {
                                redKills = Integer.parseInt(editRedKills.getText());
                                blueKills = Integer.parseInt(editBlueKills.getText());
                            }
                            Long winnerId = editWinner.getSelectionModel().getSelectedItem().getId();
                            if((winnerId.equals(redPlayer) && (redKills.compareTo(blueKills) > 0) ) || (winnerId.equals(bluePlayer) && (blueKills.compareTo(redKills) > 0)))
                            {
                                Boolean winnerChanged;
                                Boolean loserChanged;
                                Long loserId;
                                if(matchTableView.getSelectionModel().getSelectedItem().getWinnerId().equals(matchTableView.getSelectionModel().getSelectedItem().getRedSideId()))
                                    loserId=matchTableView.getSelectionModel().getSelectedItem().getBlueSideId();
                                else loserId=matchTableView.getSelectionModel().getSelectedItem().getRedSideId();
                                Match m = new Match(id, redPlayer, bluePlayer, winnerId, redKills, blueKills, redChamp, blueChamp, date);
                                Long newLoser;
                                if(m.getWinnerId().equals(m.getRedSideId()))
                                    newLoser = m.getBlueSideId();
                                else newLoser=m.getRedSideId();

                                if(m.getWinnerId().equals(matchTableView.getSelectionModel().getSelectedItem().getWinnerId()))
                                    winnerChanged=false;
                                else winnerChanged=true;
                                if(newLoser.equals(loserId))
                                    loserChanged=false;
                                else loserChanged=true;

                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setHeaderText("Are you sure?");
                                Optional<ButtonType> option = alert.showAndWait();
                                if(option.get() == ButtonType.OK && !m.equals(matchTableView.getSelectionModel().getSelectedItem()))
                                {

                                    ChangedEntity<Match> changedEntity = new ChangedEntity<>(matchTableView.getSelectionModel().getSelectedItem(), m, LocalDateTime.now());
                                    DatabaseQueries.editMatch(m, winnerChanged, loserChanged);
                                    matchTableView.setItems(FXCollections.observableList(MainApplication.matchList));
                                    MainApplication.changesList.add(changedEntity.findChanges());
                                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                                    alert2.setHeaderText("Edit made");
                                    alert2.setContentText("Successfuly edited the match");
                                    alert2.showAndWait();
                                    cancelClicked();
                                }
                                else if(m.equals(matchTableView.getSelectionModel().getSelectedItem()))
                                {
                                    Alert alert2= new Alert(Alert.AlertType.INFORMATION);
                                    alert2.setHeaderText("Cannot edit the match.");
                                    alert2.setContentText("No changes were made.");
                                    alert2.showAndWait();
                                }
                            }
                            else{

                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setContentText("Winner cannot have less kills.");
                                alert.showAndWait();
                            }

                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Entered date cannot be in the future.");
                            alert.showAndWait();
                        }
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Both champions are the same.");
                        alert.showAndWait();
                        }
                    }
                else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Both players are the same.");
                    alert.showAndWait();
                }
            }


            catch (IllegalAccessException e)
            {
                logger.debug("Error while saving the change to the list", e);

            }
            catch(NumberFormatException e)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Cannot edit the match.");
                alert.setContentText("Recheck entered numbers.");
                alert.showAndWait();
            }






        }




}
