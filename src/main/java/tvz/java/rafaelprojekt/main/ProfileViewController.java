package tvz.java.rafaelprojekt.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.USER_MODE;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileViewController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField levelTextField;
    @FXML
    private TextField winsTextField;
    @FXML
    private TextField lossesTextField;
    @FXML
    private ChoiceBox<Rank> rankChoiceBox;
    @FXML
    private Button saveButton;
    @FXML
    private ToggleButton editButton;
    @FXML
    private TableView<Match> matchTableView;
    @FXML
    private TableColumn<Match, String> redSideColumn;
    @FXML
    private TableColumn<Match, String> blueSideColumn;
    @FXML
    private TableColumn<Match, String> winnerColumn;
    @FXML
    private ImageView rankImage;
    @FXML
    private Button pfpPicker;
    @FXML
    private ImageView profileImage;
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



    private static Logger logger = LoggerFactory.getLogger(ProfileViewController.class);



    public void initialize()
    {
        for(Rank r : MainApplication.rankList)
            rankChoiceBox.getItems().add(r);
        if(MainApplication.viewedProfile.getMatchSet().isEmpty())
            matchTableView.setPlaceholder(new Label("No matches found!"));
        pfpPicker.setVisible(false);
        usernameTextField.setText(MainApplication.viewedProfile.getUsername());
        levelTextField.setText(MainApplication.viewedProfile.getLevel().toString());
        winsTextField.setText(MainApplication.viewedProfile.getWins().toString());
        lossesTextField.setText(MainApplication.viewedProfile.getLoses().toString());
        rankChoiceBox.getSelectionModel().select(MainApplication.viewedProfile.getRank());
        profileImage.setImage(MainApplication.viewedProfile.getProfilePicture());
        rankImage.setImage(new Image(MainApplication.class.getResource("images/" + MainApplication.viewedProfile.getRank() + ".png").toExternalForm()));
        redSideColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRedSide().getUsername()));
        blueSideColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBlueSide().getUsername()));
        winnerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWinner().getUsername()));
        redChampionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRedChampion().getName()));
        blueChampionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBlueChampion().getName()));
        redKillsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRedKills())));
        blueKillsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBlueKills())));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMatchDate().format(formatter)));
        List<Match> matchList = new ArrayList<>(MainApplication.viewedProfile.getMatchSet());
        matchTableView.setItems(FXCollections.observableList(matchList));
        if(MainApplication.loginMode.equals(USER_MODE.GUEST))
        {
            editButton.setDisable(true);
        }
    }

    public void editButtonClicked()
    {
        if(editButton.isSelected())
        {
            saveButton.setDisable(false);
            setEditable(true);
            rankChoiceBox.setDisable(false);

        }
        else
        {
            saveButton.setDisable(true);
            rankChoiceBox.setDisable(true);
            setEditable(false);
            initialize();

        }
    }

    public void setEditable(Boolean b)
    {
        usernameTextField.setEditable(b);
        levelTextField.setEditable(b);
        pfpPicker.setVisible(b);
        winsTextField.setEditable(b);
        lossesTextField.setEditable(b);
    }

    public void pfpPickerSelected()
    {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(MainApplication.getMainStage());
        if(selectedFile!=null)
        {
            Image image = new Image(selectedFile.toURI().toString());
            profileImage.setImage(image);
        }

    }

    public void saveButtonClicked()
    {

        try{
            Profile oldProfile = new Profile(MainApplication.viewedProfile);

            Long id = oldProfile.getId();
            String username = usernameTextField.getText();
            Image image = oldProfile.getProfilePicture();
            Integer level = Integer.valueOf(levelTextField.getText());
            Rank rank = rankChoiceBox.getSelectionModel().getSelectedItem();
            Server server = oldProfile.getServer();
            Integer wins = Integer.valueOf(winsTextField.getText());
            Integer loses = Integer.valueOf(lossesTextField.getText());
            Set<Match> matchList = oldProfile.getMatchSet();
            Profile newProfile = new Profile(id, username, image, level, rank, server, wins, loses, matchList);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Please confirm.");
            alert.setContentText("Are you sure you want to edit the profile?");
            alert.showAndWait();
            if(alert.getResult().equals(ButtonType.OK))
            {
                if(oldProfile.getUsername().equals(newProfile.getUsername()))
                {
                    if(!oldProfile.equals(newProfile))
                    {

                        DatabaseQueries.editProfile(newProfile);
                        ChangedEntity<Profile> changedEntity = new ChangedEntity<>(oldProfile, newProfile, LocalDateTime.now());
                        MainApplication.changesList.add(changedEntity.findChanges());
                        saveButton.setDisable(true);
                        rankChoiceBox.setDisable(true);
                        editButton.setSelected(false);
                        setEditable(false);
                        MainApplication.viewedProfile = newProfile;
                        matchTableView.refresh();
                        Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                        alert1.setHeaderText("Changes made.");
                        alert1.setContentText("Profile changed successfully.");
                        alert1.showAndWait();
                    }
                    else{
                        Alert alert1=new Alert(Alert.AlertType.WARNING);
                        alert1.setHeaderText("Edit cannot be made.");
                        alert1.setContentText("No changes were made.");
                        alert1.showAndWait();
                    }
                }
                else if (!ProfileGetter.usernameUsed(newProfile.getUsername())){
                    DatabaseQueries.editProfile(newProfile);
                    ChangedEntity<Profile> changedEntity = new ChangedEntity<>(oldProfile, newProfile, LocalDateTime.now());
                    MainApplication.changesList.add(changedEntity.findChanges());
                    saveButton.setDisable(true);
                    rankChoiceBox.setDisable(true);
                    editButton.setSelected(false);
                    setEditable(false);
                    MainApplication.viewedProfile = newProfile;
                    matchTableView.refresh();
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setHeaderText("Changed made.");
                    alert1.setContentText("Profile changed successfully.");
                    alert1.showAndWait();
                }
                else{

                    Alert alert1=new Alert(Alert.AlertType.WARNING);
                    alert1.setHeaderText("Cannot edit the profile");
                    alert1.setContentText("Username taken.");
                    alert1.showAndWait();
                }
            }

            }




         catch (IllegalAccessException e) {

           logger.debug("Error while saving the change to the list", e);

        }
        catch (NumberFormatException e)
        {
            logger.debug("A letter has been input instead of a number", e);
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot save the edit.");
            alert.setContentText("Please recheck the input numbers again.");
            alert.showAndWait();
        }




    }







}
