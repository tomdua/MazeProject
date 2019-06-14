package View;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class Option implements Initializable {
    public javafx.scene.control.Button exit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void closeWindow() {
        Stage s = (Stage) exit.getScene().getWindow();
        s.close();
    }


}
