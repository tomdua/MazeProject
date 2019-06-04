package View;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class About implements Initializable {
    public javafx.scene.control.Button exit;
    public javafx.scene.control.Label text;
    public javafx.scene.image.ImageView NearExit;

    public void close() {
        Platform.exit();
    }

    public void closew() {
        Stage s = (Stage) exit.getScene().getWindow();
        s.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        text.setWrapText(true);
        text.setText("Hello and welcome to Itzik and Raanan's project.\n" +
                "Here you will see our project we made in java.\n" +
                "This is a game of maze and you need to get the character " +
                "to the end of the maze where the flag is.\n" +
                "Hope you enjoy our game! good luck");
        Image SmallImageNearExit = null;
        try {
            SmallImageNearExit = new Image(new File("resources/images/exit.png").toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        NearExit.setImage(SmallImageNearExit);

    }
}