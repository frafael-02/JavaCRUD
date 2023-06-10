package tvz.java.rafaelprojekt.main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.USER_MODE;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.*;
import tvz.java.rafaelprojekt.exceptions.NoActivityException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class LeaderboardsController {

    @FXML
    private Tab EuTab;
    @FXML
    private Tab NaTab;
    @FXML
    private Tab LamTab;
    @FXML
    private TableView<Profile> profileTableView;
    @FXML
    private TableView<Profile> profileTableView1;
    @FXML
    private TableView<Profile> profileTableView2;
    @FXML
    private TableColumn<Profile, String> playersColumn;
    @FXML
    private TableColumn<Profile, String> playersColumn1;
    @FXML
    private TableColumn<Profile, String> playersColumn2;
    @FXML
    private TableColumn<Profile, String> rankColumn;
    @FXML
    private TableColumn<Profile, String> rankColumn1;
    @FXML
    private TableColumn<Profile, String> rankColumn2;
    @FXML
    private TableColumn<Profile, String> winsColumn;
    @FXML
    private TableColumn<Profile, String> winsColumn1;
    @FXML
    private TableColumn<Profile, String> winsColumn2;
    @FXML
    private TableColumn<Profile, String> lossColumn;
    @FXML
    private TableColumn<Profile, String> lossColumn1;
    @FXML
    private TableColumn<Profile, String> lossColumn2;
    @FXML
    private TableColumn<Profile, String> championsColumn;
    @FXML
    private TableColumn<Profile, String> championsColumn1;
    @FXML
    private TableColumn<Profile, String> championsColumn2;
    @FXML
    private TableColumn<Profile, String> levelColumn;
    @FXML
    private TableColumn<Profile, String> levelColumn1;
    @FXML
    private TableColumn<Profile, String> levelColumn2;
    @FXML
    private TableColumn<Profile, String> dateColumn;
    @FXML
    private TableColumn<Profile, String> dateColumn1;
    @FXML
    private TableColumn<Profile, String> dateColumn2;
    @FXML
    private TableColumn<Profile, String> winRatioColumn;
    @FXML
    private TableColumn<Profile, String> winRatioColumn1;
    @FXML
    private TableColumn<Profile, String> winRatioColumn2;
    @FXML
    private Text profilesCount;

    private List<Profile> profileListEU;

    private List<Profile> profileListNA;

    private List<Profile> profileListLAM;
    @FXML
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private TextField levelFilter;

    @FXML
    private TextField usernameFilter;

    @FXML
    private ChoiceBox<Rank> rankChoiceBox;

    @FXML
    private ChoiceBox<Champion> championChoiceBox;

    @FXML
    private RadioButton equalsRadioButton;

    @FXML
    private RadioButton greaterRadioButton;

    @FXML
    private RadioButton lessRadioButton;

    @FXML
    private TextField winsFilter;

    @FXML
    private TextField lossFilter;

    @FXML
    private DatePicker datePicker;

    public static Logger logger = LoggerFactory.getLogger(LeaderboardsController.class);




    @FXML
    private Button addButton;

    private Integer playerCount;
    public boolean threadAllow=true;



    public void initialize()
    {
        if(MainApplication.loginMode.equals(USER_MODE.GUEST))
            addButton.setDisable(true);
        profileTableView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.DELETE))
                {
                   deletePressed(profileTableView.getSelectionModel().getSelectedItem());
                }
            }
        });
        profileTableView1.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.DELETE))
                {
                    deletePressed(profileTableView1.getSelectionModel().getSelectedItem());
                }
            }
        });
        profileTableView2.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.DELETE))
                {
                    deletePressed(profileTableView2.getSelectionModel().getSelectedItem());
                }
            }
        });


        championChoiceBox.getItems().addAll(MainApplication.championList);

        profileListEU = ProfileGetter.getByServer("EU");
        profileListEU = ProfileGetter.sortPlayers(profileListEU);
        profileListNA = ProfileGetter.getByServer("NA");
        profileListNA = ProfileGetter.sortPlayers(profileListNA);
        profileListLAM = ProfileGetter.getByServer("LAM");
        profileListLAM = ProfileGetter.sortPlayers(profileListLAM);

        for(Rank r : MainApplication.rankList)
        {
            rankChoiceBox.getItems().add(r);
        }

        playersColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        rankColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRank().name()));
        winsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getWins())));
        lossColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLoses())));
        levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLevel())));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        dateColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(ProfileGetter.lastActivity(cellData.getValue().getMatchSet()).format(formatter));
            } catch (NoActivityException e) {
                return new SimpleStringProperty("No activity");
            }
        });
        winRatioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getWinRate()) + "%"));
        championsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ProfileGetter.mostPlayedChampions(cellData.getValue())));
        profileTableView.setItems(FXCollections.observableList(profileListEU));


        playersColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        rankColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRank().name()));
        winsColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getWins())));
        lossColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLoses())));
        levelColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLevel())));
        dateColumn1.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(ProfileGetter.lastActivity(cellData.getValue().getMatchSet()).format(formatter));
            } catch (NoActivityException e) {
                return new SimpleStringProperty("No activity");
            }
        });
        championsColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(ProfileGetter.mostPlayedChampions(cellData.getValue())));
        winRatioColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getWinRate()) + "%"));
        profileTableView1.setItems(FXCollections.observableList(profileListNA));
        playersColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        rankColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRank().name()));
        winsColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getWins())));
        lossColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLoses())));
        levelColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLevel())));
        dateColumn2.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(ProfileGetter.lastActivity(cellData.getValue().getMatchSet()).format(formatter));
            } catch (NoActivityException e) {
                return new SimpleStringProperty("No activity");
            }
        });
        championsColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(ProfileGetter.mostPlayedChampions(cellData.getValue())));
        winRatioColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getWinRate()) + "%"));
        profileTableView2.setItems(FXCollections.observableList(profileListLAM));

        Thread countSetThread = new Thread() {
            @Override
            public void run()
            {
                try{
                    setCount();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException();
                }


            }
        };

        Thread countDisplayThread = new Thread()
        {
            @Override
            public void run()
            {
                try{displayCount();}
                catch(InterruptedException e)
                {
                    throw new RuntimeException();
                }

            }
        };

        Timeline setCountTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1),new EventHandler<ActionEvent>()
                { @Override
                public void handle(ActionEvent event)
                { Platform.runLater(countSetThread);

                } }
                ));
       setCountTimeline.setCycleCount(Timeline.INDEFINITE);
        setCountTimeline.play();

        Timeline displayCountTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1),new EventHandler<ActionEvent>()
        { @Override
        public void handle(ActionEvent event)
        { Platform.runLater(countDisplayThread);

        } }
        ));
        displayCountTimeline.setCycleCount(Timeline.INDEFINITE);
        displayCountTimeline.play();
    }




    public void displayCount() throws InterruptedException
    {
        profilesCount.setText(String.valueOf(getPlayerCount()));
    }



    public void setCount() throws InterruptedException
    {
        if(EuTab.isSelected())
        {
            if(profilesCount.getText().isEmpty())
            {
                setPlayerCount(profileListEU.size());
            }
            else if(!getPlayerCount().equals(profileListEU.size()))
                setPlayerCount(profileListEU.size());

        }
        else if(NaTab.isSelected())
        {
            if(!getPlayerCount().equals(profileListNA.size()))
                setPlayerCount(profileListNA.size());

        }

        else
        {
            if(!getPlayerCount().equals(profileListLAM.size()))
                setPlayerCount(profileListLAM.size());
        }



    }

    public void filterClicked()
    {
        List<Profile> filteredList;
        try{if(EuTab.isSelected())
        {
            filteredList=profileListEU;

            if(!usernameFilter.getText().isEmpty())
            {
                Optional<Profile> result =
                        filteredList.stream().filter(profile -> profile.getUsername().equals(usernameFilter.getText())).findFirst();
                if(result.isPresent())
                {
                    profileTableView.getSelectionModel().select(result.get());

                }
                else
                    profileTableView.getSelectionModel().clearSelection();
            }
            if(!rankChoiceBox.getSelectionModel().isEmpty())
            {
                filteredList= filteredList.stream().filter(profile -> profile.getRank().name().equals(rankChoiceBox.getSelectionModel().getSelectedItem().name())).toList();
            }
            if(!championChoiceBox.getSelectionModel().isEmpty())
            {
                filteredList = filteredList.stream().filter(profile -> ProfileGetter.mostPlayedChampions(profile).contains(championChoiceBox.getSelectionModel().getSelectedItem().getName())).toList();
            }
            if(datePicker.getValue()!=null)
            {
                filteredList=filteredList.stream().filter(profile -> {
                    try {
                        return ProfileGetter.lastActivity(profile.getMatchSet()).equals(datePicker.getValue());
                    } catch (NoActivityException e) {
                        return false;
                    }
                }).toList();
            }
            if(!levelFilter.getText().isEmpty())
            {
                if(equalsRadioButton.isSelected())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getLevel().equals(Integer.valueOf(levelFilter.getText()))).toList();
                }
                else if(greaterRadioButton.isSelected())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getLevel().compareTo(Integer.valueOf(levelFilter.getText()))>0).toList();
                }
                else if (lessRadioButton.isSelected())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getLevel().compareTo(Integer.valueOf(levelFilter.getText()))<0).toList();

                }
            }
            if(!winsFilter.getText().isEmpty())
            {
                filteredList=filteredList.stream().filter(profile -> profile.getWins().equals(Integer.valueOf(winsFilter.getText()))).toList();
            }
            if(!lossFilter.getText().isEmpty())
            {
                filteredList=filteredList.stream().filter(profile -> profile.getLoses().equals(Integer.valueOf(lossFilter.getText()))).toList();
            }

            profileTableView.setItems(FXCollections.observableList(filteredList));
        }

            if(NaTab.isSelected())
            {
                filteredList=profileListNA;

                if(!usernameFilter.getText().isEmpty())
                {
                    Optional<Profile> result =
                            filteredList.stream().filter(profile -> profile.getUsername().equals(usernameFilter.getText())).findFirst();
                    if(result.isPresent())
                    {
                        profileTableView1.getSelectionModel().select(result.get());

                    }
                    else
                        profileTableView1.getSelectionModel().clearSelection();
                }
                if(!rankChoiceBox.getSelectionModel().isEmpty())
                {
                    filteredList= filteredList.stream().filter(profile -> profile.getRank().name().equals(rankChoiceBox.getSelectionModel().getSelectedItem().name())).toList();
                }
                if(!championChoiceBox.getSelectionModel().isEmpty())
                {
                    filteredList = filteredList.stream().filter(profile -> ProfileGetter.mostPlayedChampions(profile).contains(championChoiceBox.getSelectionModel().getSelectedItem().getName())).toList();
                }
                if(datePicker.getValue()!=null)
                {
                    filteredList=filteredList.stream().filter(profile -> {
                        try {
                            return ProfileGetter.lastActivity(profile.getMatchSet()).equals(datePicker.getValue());
                        } catch (NoActivityException e) {
                            return false;
                        }
                    }).toList();

                }
                if(!levelFilter.getText().isEmpty())
                {
                    if(equalsRadioButton.isSelected())
                    {
                        filteredList=filteredList.stream().filter(profile -> profile.getLevel().equals(Integer.valueOf(levelFilter.getText()))).toList();
                    }
                    else if(greaterRadioButton.isSelected())
                    {
                        filteredList=filteredList.stream().filter(profile -> profile.getLevel().compareTo(Integer.valueOf(levelFilter.getText()))>0).toList();
                    }
                    else if (lessRadioButton.isSelected())
                    {
                        filteredList=filteredList.stream().filter(profile -> profile.getLevel().compareTo(Integer.valueOf(levelFilter.getText()))<0).toList();

                    }
                }
                if(!winsFilter.getText().isEmpty())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getWins().equals(Integer.valueOf(winsFilter.getText()))).toList();
                }
                if(!lossFilter.getText().isEmpty())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getLoses().equals(Integer.valueOf(lossFilter.getText()))).toList();
                }

                profileTableView1.setItems(FXCollections.observableList(filteredList));
            }

            if(LamTab.isSelected())
            {
                filteredList=profileListLAM;

                if(!usernameFilter.getText().isEmpty())
                {
                    Optional<Profile> result =
                            filteredList.stream().filter(profile -> profile.getUsername().equals(usernameFilter.getText())).findFirst();
                    if(result.isPresent())
                    {
                        profileTableView2.getSelectionModel().select(result.get());

                    }
                    else
                        profileTableView2.getSelectionModel().clearSelection();
                }
                if(!rankChoiceBox.getSelectionModel().isEmpty())
                {
                    filteredList= filteredList.stream().filter(profile -> profile.getRank().name().equals(rankChoiceBox.getSelectionModel().getSelectedItem().name())).toList();
                }
                if(!championChoiceBox.getSelectionModel().isEmpty())
                {
                    filteredList = filteredList.stream().filter(profile -> ProfileGetter.mostPlayedChampions(profile).contains(championChoiceBox.getSelectionModel().getSelectedItem().getName())).toList();
                }
                if(datePicker.getValue()!=null)
                {
                    filteredList=filteredList.stream().filter(profile -> {
                        try {
                            return ProfileGetter.lastActivity(profile.getMatchSet()).equals(datePicker.getValue());
                        } catch (NoActivityException e) {
                            return false;
                        }
                    }).toList();
                }
                if(!levelFilter.getText().isEmpty())
                {
                    if(equalsRadioButton.isSelected())
                    {
                        filteredList=filteredList.stream().filter(profile -> profile.getLevel().equals(Integer.valueOf(levelFilter.getText()))).toList();
                    }
                    else if(greaterRadioButton.isSelected())
                    {
                        filteredList=filteredList.stream().filter(profile -> profile.getLevel().compareTo(Integer.valueOf(levelFilter.getText()))>0).toList();
                    }
                    else if (lessRadioButton.isSelected())
                    {
                        filteredList=filteredList.stream().filter(profile -> profile.getLevel().compareTo(Integer.valueOf(levelFilter.getText()))<0).toList();

                    }
                }
                if(!winsFilter.getText().isEmpty())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getWins().equals(Integer.valueOf(winsFilter.getText()))).toList();
                }
                if(!lossFilter.getText().isEmpty())
                {
                    filteredList=filteredList.stream().filter(profile -> profile.getLoses().equals(Integer.valueOf(lossFilter.getText()))).toList();
                }

                profileTableView2.setItems(FXCollections.observableList(filteredList));
            }}
        catch (NumberFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot filter profiles");
            alert.setContentText("Recheck entered numbers.");
            alert.showAndWait();
        }


    }

    public void resetButtonClicked() {
        usernameFilter.clear();
        rankChoiceBox.getSelectionModel().clearSelection();
        championChoiceBox.getSelectionModel().clearSelection();
        winsFilter.clear();
        lossFilter.clear();
        levelFilter.clear();
        datePicker.setValue(null);
        if (EuTab.isSelected()) {
            profileTableView.getSelectionModel().clearSelection();
            profileTableView.setItems(FXCollections.observableList(profileListEU));
        } else if (NaTab.isSelected()) {
            profileTableView1.getSelectionModel().clearSelection();
            profileTableView1.setItems(FXCollections.observableList(profileListNA));
        } else if (LamTab.isSelected())
        { profileTableView2.getSelectionModel().clearSelection();
        profileTableView2.setItems(FXCollections.observableList(profileListLAM));}


    }

    public void addNewClicked() throws IOException
    {

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("newProfile.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 630, 400);
            MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
            MainApplication.getMainStage().setScene(scene);
            MainApplication.getMainStage().show();

    }
public void deletePressed(Profile profile)
    {
        if(profile !=null)
        {
            Long id = profile.getId();
            DatabaseQueries.deleteProfile(id);
            ChangedEntity<Profile> changedEntity = new ChangedEntity<>(profile, null, LocalDateTime.now());
            try {
                MainApplication.changesList.add(changedEntity.findChanges());
            } catch (IllegalAccessException e) {

                logger.debug(e.getMessage(), e);
                throw new RuntimeException("Error while trying to save the change to the list");
            }
            Alert alert1=new Alert(Alert.AlertType.INFORMATION);
            alert1.setHeaderText("Profile deleted");
            alert1.setContentText("Profile deleted successfully.");
            alert1.showAndWait();
            if(EuTab.isSelected())
            {
                profileListEU=ProfileGetter.getByServer("EU");
                profileListEU=ProfileGetter.sortPlayers(profileListEU);
                profileTableView.setItems(FXCollections.observableList(profileListEU));
            }
            if(NaTab.isSelected())
            {
                profileListNA=ProfileGetter.getByServer("NA");
                profileListNA=ProfileGetter.sortPlayers(profileListNA);
                profileTableView1.setItems(FXCollections.observableList(profileListNA));
            }
            if(LamTab.isSelected())
            {
                profileListLAM=ProfileGetter.getByServer("LAM");
                profileListLAM=ProfileGetter.sortPlayers(profileListLAM);
                profileTableView2.setItems(FXCollections.observableList(profileListLAM));
            }

        }

    }

    public synchronized Integer getPlayerCount() throws InterruptedException {
        while(threadAllow == true)
        {
            wait();
        }
        threadAllow=true;
        Integer result = playerCount;
       notify();
       return result;
    }

    public synchronized void setPlayerCount(Integer playerCount) throws InterruptedException {
        while(threadAllow == true)
        {
            wait();
        }

        threadAllow=false;
        this.playerCount = playerCount;
        notify();
    }
}
