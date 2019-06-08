package View;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class About implements Initializable {
    public javafx.scene.control.Button exit;
    public javafx.scene.control.Label text;
    public javafx.scene.image.ImageView NearExit;


    public void closeWindow() {
        Stage s = (Stage) exit.getScene().getWindow();
        s.close();
    }


    public void initialize(URL location, ResourceBundle resources) {
    }

}