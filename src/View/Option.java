package View;

import Server.Configurations;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class Option implements Initializable {
    public javafx.scene.control.Button exit;
    public javafx.scene.control.Button save;
    public ChoiceBox algo;
    public ChoiceBox maze;
    public ChoiceBox thread;
    String algor="BFS";
    String mazeP="MyMazeGenerator";
    String core="2";

    public void close() {
        Platform.exit();
    }

    public void closew() {
        Stage s = (Stage) exit.getScene().getWindow();
        s.close();
    }

    public void save() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(String.format("Settings Saved \n "+ "\n Algorithem is: "+algor+ "\n MazeType is: "+mazeP+ "\n Number of cores: "+core));
        alert.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Configurations.config();

        algo.getItems().addAll("BFS", "DFS");
        maze.getItems().addAll("SimpleMaze", "MyMazeGenerator");
        thread.getItems().addAll("1", "2", "3", "4");

    }

    public void SetConf() throws IOException {
        OutputStream output = null;
        InputStream input = null;
        String text = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./resources/config.properties"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line + ",");
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            text = stringBuilder.toString();
            bufferedReader.close();
        } catch (IOException e) {
        }
        if (input == null) {//check if file exthist
            output = new FileOutputStream("Resources/config.properties");
            Properties prop = new Properties(); //create new prop file
            if (algo.getValue()==(String)"BFS")
                algor="BreadthFirstSearch";
            if (algo.getValue()==(String)"DFS")
                algor="DepthFirstSearch";
            if (maze.getValue()==(String)"SimpleMaze")
                mazeP="SimpleMazeGenerator";
            if (maze.getValue()==(String)"MyMazeGenerator")
                mazeP="MyMazeGenerator";
            if (thread.getValue()==(String)"1")
                core="1";
            if (thread.getValue()==(String)"2")
                core="2";
            if (thread.getValue()==(String)"3")
                core="3";
            if (thread.getValue()==(String)"4")
                core="4";

            prop.setProperty("MazeAlgoType", algor);
            prop.setProperty("numberCores", core);
            prop.setProperty("MazeType", mazeP);

            // save properties to project root folder
            prop.store(output, null);
        }

        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        save();
    }
}
