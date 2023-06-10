package tvz.java.rafaelprojekt.main;



import animatefx.animation.ZoomIn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.data.Data;
import tvz.java.rafaelprojekt.data.USER_MODE;
import tvz.java.rafaelprojekt.exceptions.LoginDataException;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LoginController {

    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Text failedLoginText;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    public void initialize()
    {


        MainApplication.loginMode = USER_MODE.NOT_LOGGED_IN;
        ImageView buttonImage = new ImageView(getClass().getResource("images/arrow.png").toExternalForm());
        buttonImage.fitWidthProperty().bind(loginButton.prefWidthProperty());
        buttonImage.fitHeightProperty().bind(loginButton.prefHeightProperty());
        buttonImage.setPreserveRatio(true);
        loginButton.setGraphic(buttonImage);
        loginButton.setEffect(new DropShadow());
        MainApplication.getMainStage().setResizable(false);

    }

    public void loginClicked() {
        if (!passwordTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty()) {
            String password = Data.hashPassword(passwordTextField.getText());

            for (Map.Entry<String, String> entry : MainApplication.loginData.entrySet()) {
                if (password.equals(entry.getValue()) && usernameTextField.getText().equals(entry.getKey())) {

                    try {
                        if (usernameTextField.getText().equals("admin"))
                            MainApplication.loginMode = USER_MODE.ADMIN;

                        else MainApplication.loginMode = USER_MODE.GUEST;

                        MainApplication.loggedUser = entry.getKey();
                        showScreen();
                    } catch (IOException e) {
                        logger.debug("Error while logging in", e);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error while trying to log in.");
                        alert.setContentText("Servers are currently down, try again later");

                    }
                }
                else {
                    failedLoginText.setText("Wrong username or password. Try again.");
                    failedLoginText.setLayoutX(517);
                    failedLoginText.setLayoutY(389);
                    failedLoginText.setVisible(true);
                }
            }
        } else {
            failedLoginText.setLayoutX(544);
            failedLoginText.setLayoutY(389);
            failedLoginText.setText("Enter username and password.");
            failedLoginText.setVisible(true);
        }

    }

    public void registerClicked()
    {
        if (!passwordTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty())
        {

           try{
               boolean register=  Data.register(usernameTextField.getText(), Data.hashPassword(passwordTextField.getText()));

            if(register)
            {
                failedLoginText.setText("Register succesfull! Please login with your username and password.");
                failedLoginText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                MainApplication.changesList.add("New user registered:" + usernameTextField.getText() +" " + LocalDateTime.now().format(formatter));
                MainApplication.loginData = Data.getLoginProfiles();

            }
            else if(!register)
               {
                   failedLoginText.setText("Username taken or not allowed.");
                   failedLoginText.setVisible(true);
               }
           }
           catch (LoginDataException e) {
               logger.debug(e.getMessage(), e);
               throw new RuntimeException(e.getMessage());
           }
        }
        else  {
            failedLoginText.setLayoutX(544);
            failedLoginText.setLayoutY(389);
            failedLoginText.setText("Enter username and password.");
            failedLoginText.setVisible(true);
        }

    }

    public static void showScreen() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("search.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        MainApplication.getMainStage().setTitle("User mode: " + MainApplication.loginMode + " user: " + MainApplication.loggedUser);
        MainApplication.getMainStage().setScene(scene);
        new ZoomIn(scene.getRoot()).play();
        MainApplication.getMainStage().show();
    }
}