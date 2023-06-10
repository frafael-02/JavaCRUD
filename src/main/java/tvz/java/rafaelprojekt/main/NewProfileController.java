package tvz.java.rafaelprojekt.main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.database.DatabaseQueries;
import tvz.java.rafaelprojekt.entity.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;

public class NewProfileController {
    private static Logger logger = LoggerFactory.getLogger(NewProfileController.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox<String> serverChoiceBox;
    @FXML
    private ChoiceBox<Rank> rankChoiceBox;
    @FXML
    private TextField levelTextField;
    @FXML
    private TextField winsTextField;
    @FXML
    private TextField losesTextField;
    @FXML
    private ImageView imageView;

    @FXML
    private CheckBox apiUsername;

    public void initialize()
    {
        for(Server s : Server.values())
        {
            serverChoiceBox.getItems().add(s.getCode());
        }
        for(Rank r : MainApplication.rankList)
        {
            rankChoiceBox.getItems().add(r);
        }


    }

    public void pfpPickerSelected()
    {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(MainApplication.getMainStage());
        if(selectedFile != null)
        {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }


    }

    public void addClicked()
    {
        if(!usernameTextField.getText().isEmpty() && !winsTextField.getText().isEmpty() && !losesTextField.getText().isEmpty() && !levelTextField.getText().isEmpty() && !rankChoiceBox.getSelectionModel().isEmpty() && !serverChoiceBox.getSelectionModel().isEmpty()) {
            Integer level = 0;
            Integer wins = 0;
            Integer loses = 0;

            try {
                level = Integer.valueOf(levelTextField.getText());
                wins = Integer.valueOf(winsTextField.getText());
                loses = Integer.valueOf(losesTextField.getText());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Please confirm.");
                alert.setContentText("Are you sure you want to add the profile?");
                alert.showAndWait();
                if (alert.getResult().equals(ButtonType.OK)) {
                    Server server = null;
                    for (Server s : Server.values()) {
                        if (s.getCode().equals(serverChoiceBox.getSelectionModel().getSelectedItem()))
                            server = s;

                    }

                    if(!ProfileGetter.usernameUsed(usernameTextField.getText()))
                    {
                        Profile profile = new Profile(0L, usernameTextField.getText(), imageView.getImage(), level, rankChoiceBox.getSelectionModel().getSelectedItem(), server, wins, loses, new HashSet<>());
                        DatabaseQueries.addProfile(profile);
                        ChangedEntity<Profile> changedEntity = new ChangedEntity<>(null, profile, LocalDateTime.now());
                        MainApplication.changesList.add(changedEntity.findChanges());
                        Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                        alert1.setHeaderText("New profile added.");
                        alert1.setContentText("Profile added successfully.");
                        alert1.showAndWait();
                    }
                    else{
                        Alert alert1 = new Alert(Alert.AlertType.WARNING);
                        alert1.setHeaderText("Cannot add the profile.");
                        alert1.setContentText("Username already taken.");
                        alert1.showAndWait();
                    }

                }
            } catch (NumberFormatException e) {
                logger.debug("A letter has been input instead of a number", e);
                Alert alert2 = new Alert(Alert.AlertType.WARNING);
                alert2.setHeaderText("Cannot save the edit.");
                alert2.setContentText("Please recheck the input numbers again.");
                alert2.showAndWait();
            } catch (IllegalAccessException e) {
                logger.debug("Error while saving a change to the list", e);

            }

        }

        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Cannot save the profile!");
            alert.setContentText("Please fill every field.");
            alert.showAndWait();
        }




    }

    public void resetClicked()
    {
        apiUsername.setSelected(false);
        usernameTextField.clear();
        serverChoiceBox.getSelectionModel().clearSelection();
        rankChoiceBox.getSelectionModel().clearSelection();
        levelTextField.clear();
        winsTextField.clear();
        losesTextField.clear();
        imageView.setImage(new Image("C:\\Users\\Rafael\\IdeaProjects\\Rafael-Projekt\\src\\main\\resources\\tvz\\java\\rafaelprojekt\\main\\images\\loginIcon.png"));
    }

    public void randomClicked(){
        Random random = new Random();
    if(!apiUsername.isSelected())
    {
        int leftLimit = 48;
        int rightLimit = 122;

        int targetStringLength = random.nextInt(10);
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        usernameTextField.setText(generatedString);
    }
    else{
        try {
            URL url = new URL("https://random-data-api.com/api/v2/users?response_type=json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            JSONObject json = new JSONObject(content.toString());
            usernameTextField.setText(json.getString("username"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



       imageView.setImage(new Image("https://picsum.photos/200"));


        int numbers[] = new int[3];
        for(int i=0;i<3;i++)
        {
            int minimum = 1;
            int maximum = 40;
            int randomInt = minimum + random.nextInt(maximum - minimum + 1);
            numbers[i] = randomInt;
        }
        levelTextField.setText(String.valueOf(numbers[0]));
        winsTextField.setText(String.valueOf(numbers[1]));
        losesTextField.setText(String.valueOf(numbers[2]));
        rankChoiceBox.getSelectionModel().select(random.nextInt(3));
        serverChoiceBox.getSelectionModel().select(random.nextInt(3));




    }
    //username, pfp, level, rank, server, wins, loses, matchSet

}
