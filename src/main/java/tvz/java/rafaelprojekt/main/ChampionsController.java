package tvz.java.rafaelprojekt.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.USER_MODE;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.Champion;
import tvz.java.rafaelprojekt.entity.ChangedEntity;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public class ChampionsController {
    public static Logger logger = LoggerFactory.getLogger(ChampionsController.class);

    @FXML
    private TextField nameTextField;
    @FXML
    private TableView<Champion> tableView;

    public static TableView<Champion> tableviewstatic;
    @FXML
    private TableColumn<Champion, String> nameColumn;
    @FXML
    private TableColumn<Champion, String> playedTimesColumn;
    @FXML
    private TableColumn<Champion, String> winRateColumn;
    @FXML
    private TextField timesPlayedTextField;
    @FXML
    private TextField winrateTextField;
    @FXML
    private ListView<String> profileSelect;

    @FXML
    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private ToggleGroup toggleGroup1 = new ToggleGroup();
    @FXML
    private RadioButton equalsPlayed;
    @FXML
    private RadioButton morePlayed;
    @FXML
    private RadioButton lessPlayed;
    @FXML
    private RadioButton equalsWinrate;
    @FXML
    private RadioButton lessWinrate;
    @FXML
    private RadioButton moreWinrate;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextField editUsername;

    @FXML
    private AnchorPane editPane;

    @FXML
    private Tab tab;

    @FXML
    private Button addButton;


    public void initialize()
    {
        if(MainApplication.loginMode.equals(USER_MODE.GUEST))
            addButton.setDisable(true);

        List<String> nameList = MainApplication.profileList.stream().map(profile -> profile.getUsername()).toList();
        profileSelect.getItems().addAll(nameList);
        winRateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWinRate() + "%"));
        playedTimesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPlayedTimes())));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        tableView.setItems(FXCollections.observableList(MainApplication.championList));
        profileSelect.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableviewstatic=tableView;


    }

    public void filterClicked()
    {
        List<Champion> filteredList = MainApplication.championList;
        filteredList = filteredList.stream().filter(champ -> champ.getName().contains(nameTextField.getText())).toList();
        if(!winrateTextField.getText().isEmpty())
        {
            if(equalsWinrate.isSelected())
            {
                filteredList=filteredList.stream().filter(champ -> champ.getWinRate()==Integer.parseInt(winrateTextField.getText())).toList();
            }
            if(lessWinrate.isSelected())
            {
                filteredList=filteredList.stream().filter(champ -> champ.getWinRate()<Integer.parseInt(winrateTextField.getText())).toList();
            }
            if(moreWinrate.isSelected())
            {
                filteredList=filteredList.stream().filter(champ -> champ.getWinRate()>Integer.parseInt(winrateTextField.getText())).toList();
            }
        }
        if(!timesPlayedTextField.getText().isEmpty())
        {
            if(equalsPlayed.isSelected())
            {
                filteredList=filteredList.stream().filter(champ -> champ.getPlayedTimes()==Integer.parseInt(timesPlayedTextField.getText())).toList();
            }
            if(lessPlayed.isSelected())
            {
                filteredList=filteredList.stream().filter(champ -> champ.getPlayedTimes()<Integer.parseInt(timesPlayedTextField.getText())).toList();
            }
            if(morePlayed.isSelected())
            {
                filteredList=filteredList.stream().filter(champ -> champ.getPlayedTimes()>Integer.parseInt(timesPlayedTextField.getText())).toList();
            }
        }
        if(!profileSelect.getSelectionModel().isEmpty())
        {
            for(String s :profileSelect.getSelectionModel().getSelectedItems())
            {
                filteredList=filteredList.stream().filter(champ -> champ.wasPlayedBy(s)).toList();
            }
        }



        tableView.setItems(FXCollections.observableList(filteredList));
    }

    public void resetClicked()
    {
        profileSelect.getSelectionModel().clearSelection();
        tableView.getSelectionModel().clearSelection();
        nameTextField.clear();
        winrateTextField.clear();
        timesPlayedTextField.clear();
        if(toggleGroup.getSelectedToggle()!=null)
            toggleGroup.getSelectedToggle().setSelected(false);
        if(toggleGroup1.getSelectedToggle()!=null)
            toggleGroup1.getSelectedToggle().setSelected(false);
        tableView.setItems(FXCollections.observableList(MainApplication.championList));

    }
    public void editClicked()
    {

        if(!tableView.getSelectionModel().isEmpty())
        {
            tabPane.getTabs().add(tab);
            editPane.setVisible(true);
            rootPane.setDisable(true);
            editUsername.setText(tableView.getSelectionModel().getSelectedItem().getName());
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot edit the champion");
            alert.setContentText("No champion has been selected.");
            alert.showAndWait();
        }
    }
    public void saveClicked()
    {
        if(!editUsername.getText().isEmpty() && !editUsername.getText().equals(tableView.getSelectionModel().getSelectedItem().getName()))        {
            if(!Champion.nameUsed(editUsername.getText()))
            {
                Champion oldChamp = new Champion(tableView.getSelectionModel().getSelectedItem().getId(), tableView.getSelectionModel().getSelectedItem().getName());
                Champion newChamp = new Champion(tableView.getSelectionModel().getSelectedItem().getId(), editUsername.getText());
                tableView.getSelectionModel().getSelectedItem().setName(editUsername.getText());
                tableView.refresh();
                DatabaseQueries.editChampion(editUsername.getText(), tableView.getSelectionModel().getSelectedItem().getId());
                ChangedEntity<Champion> changedEntity = new ChangedEntity<>(oldChamp, newChamp, LocalDateTime.now());
                try {
                    MainApplication.changesList.add(changedEntity.findChanges());
                } catch (IllegalAccessException e) {

                    logger.debug(e.getMessage(), e);
                }
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setHeaderText("Changes made.");
                alert1.setContentText("Champion changed successfully.");
                alert1.showAndWait();
            }
            else{
                Alert alert1=new Alert(Alert.AlertType.WARNING);
                alert1.setHeaderText("Cannot edit the champion.");
                alert1.setContentText("Name is taken.");
                alert1.showAndWait();
            }

        }
        else{
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot edit the champion.");
            alert.setContentText("No changes were made.");
            alert.showAndWait();
        }
    }

    public void closeClicked()
    {

        editPane.setVisible(false);
        rootPane.setDisable(false);
    }

    public void deleteClicked()
    {
        if(!tableView.getSelectionModel().isEmpty() && !Champion.isUsed(tableView.getSelectionModel().getSelectedItem().getId()))
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Please confirm or cancel");
            alert.setContentText("Are you sure you want to delete the champion?");
            alert.showAndWait();
            if(alert.getResult().equals(ButtonType.OK))
            {
                DatabaseQueries.deleteChampion(tableView.getSelectionModel().getSelectedItem().getId());
                ChangedEntity<Champion> changedEntity = new ChangedEntity<>(tableView.getSelectionModel().getSelectedItem(), null, LocalDateTime.now());
                try {
                    MainApplication.changesList.add(changedEntity.findChanges());
                } catch (IllegalAccessException e) {
                    logger.debug(e.getMessage(),e);

                }
            }


            tableView.setItems(FXCollections.observableList(MainApplication.championList));


        }
        else if (!tableView.getSelectionModel().isEmpty() && Champion.isUsed(tableView.getSelectionModel().getSelectedItem().getId())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot delete used champion!");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("No champion selected!");
            alert.showAndWait();
        }
    }

    public void addClicked() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("newChampion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 116);
        Stage stage = new Stage();
        stage.setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        MainApplication.getMainStage().hide();
    }


}
