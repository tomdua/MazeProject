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
        text.setText("Welcome to Game Of Thorns Maze by Almog & Tom \n" +
                "Here they compete for the iron throne!" +
                "To win, you have to go to the throne. Just beware of the walls!\n" +
                "Good luck and not kill in the way");
    }
}