package View;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Help implements Initializable {
    public javafx.scene.control.Button exit;

    public void closeWindow() {
        Stage s = (Stage) exit.getScene().getWindow();
        s.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}