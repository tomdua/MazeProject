package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {

    private static final int startTime = 10;
    private static final String startLives = "* * *";
    @FXML
    private static MyViewModel viewModel = new MyViewModel(new MyModel());
    private final StringProperty lives = new SimpleStringProperty(startLives);
    private final IntegerProperty timeSeconds = new SimpleIntegerProperty(startTime);
    public MazeDisplay mazeDisplay = new MazeDisplay();
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();
    public javafx.scene.control.TextField txt_row;
    public javafx.scene.control.TextField txt_col;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Label lbl_timeLeft;
    public javafx.scene.control.Label lbl_livesLeft;
    public javafx.scene.control.Button btn_GenerateMaze;
    public javafx.scene.control.Button btn_SolveMaze;
    public javafx.scene.control.Button btn_StopMusic;
    public ChoiceBox cbBCharacter;

    int mazeNum = 0;
    boolean showOnce = false;
    boolean songOnce = true;
    private Timeline time;
    private MediaPlayer mediaPlayer;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
       btn_GenerateMaze.setVisible(false);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_timeLeft.textProperty().bind(timeSeconds.asString());
        lbl_livesLeft.textProperty().bind(lives);
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            mazeDisplay.setMaze(viewModel.getMaze());
            mazeDisplay.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
            mazeDisplay.setGoalPosition(viewModel.getEndPosition());
            displayMaze(viewModel.getMaze());
            // btn_GenerateMaze.setDisable(false);
            if (viewModel.gameFinish() && !showOnce) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Well Done,\n"+
                                    "You'r the king of the seven kingdoms");
                Music(1);
                alert.show();
                btn_GenerateMaze.setDisable(false);
                showOnce = true;
            }
            mazeDisplay.redraw();
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplay.setCharacterPosition(characterPositionRow, characterPositionColumn);
        mazeDisplay.endPosition(viewModel.getEndPosition());
        mazeDisplay.Solved(viewModel.getMazeSolutionArr());
        mazeDisplay.isSolved(viewModel.isSolved());
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        if (viewModel.isSolved())
            mazeDisplay.redraw();
        //  btn_GenerateMaze.setDisable(false);
    }

    public void generateMaze() {
        if (songOnce == true)
            Music(0);
        btn_StopMusic.setVisible(true);
        btn_GenerateMaze.setDisable(true);
        lives.setValue("* * *");
        if (time != null)
            time.stop();
        Timer();
        showOnce = false;
        int height, width;
        try {
            height = Integer.valueOf(txt_row.getText());
            if (height <= 0) {
                height = 10;
                txt_row.setText("10");
            }
        } catch (Exception e) {
            height = 10;
            txt_row.setText("10");
        }
        try {
            width = Integer.valueOf(txt_col.getText());
            if (width <= 0) {
                width = 10;
                txt_col.setText("10");
            }
        } catch (Exception e) {
            width = 10;
            txt_col.setText("10");
        }
        int[][] temp = viewModel.generateMaze(height, width);
        mazeDisplay.setMaze(temp);
        mazeDisplay.endPosition(viewModel.getEndPosition());
        btn_SolveMaze.setVisible(true);
        displayMaze(temp);
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }


    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplay.requestFocus();
    }


    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplay.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplay.redraw();
            }
        });
    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.getSolution(this.viewModel, this.viewModel.getCharacterPositionRow(), this.viewModel.getCharacterPositionColumn(), "solve");
    }


    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 300, 165);
            scene.getStylesheets().add("box.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println("Error About.fxml not found");
        }
    }

    public void Help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add("box.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println("Error Help.fxml not found");
        }
    }


    public void Option(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Option");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Option.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add("box.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println("Error Option.fxml not found");
        }
    }

    public void saveGame() {
        FileChooser fc = new FileChooser();
        File filePath = new File("./Mazes/");
        if (!filePath.exists())
            filePath.mkdir();
        fc.setTitle("Saving maze");
        fc.setInitialFileName("Maze Number " + mazeNum + "");
        mazeNum++;
        fc.setInitialDirectory(filePath);
        File file = fc.showSaveDialog((Stage) mazeDisplay.getScene().getWindow());
        if (file != null)
            viewModel.save(file);
    }

    public void loadGame() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Loading maze");
        File filePath = new File("./Mazes/");
        if (!filePath.exists())
            filePath.mkdir();
        fc.setInitialDirectory(filePath);
        File file = fc.showOpenDialog(new PopupWindow() {
        });
        if (file != null && file.exists() && !file.isDirectory()) {
            viewModel.load(file);
            if (songOnce == true)
                Music(0);
            mazeDisplay.redraw();
        }
    }


    public void Music(int x) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        String path;
        if (x == 0) {
            songOnce = false;
            path = "resources\\start.mp3";
        } else {
            songOnce = true;
            path = "resources\\end.mp3";
        }
        Media temp = new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(temp);
        mediaPlayer.play();
    }


    public void updateTime() {
        int seconds = timeSeconds.get();
        timeSeconds.set(seconds - 1);
        if (timeSeconds.get() <= 0) {
            viewModel.setCharacterPositionColumn(0);
            viewModel.setCharacterPositionRow(0);
            mazeDisplay.setCharacterPosition(0, 0);
            mazeDisplay.redraw();
            time.stop();
            updateLives();
            timeSeconds.set(startTime);
            if (!lives.get().equals("")) {
                //  Stage SettingsStage = (Stage) btnSignOut.getScene().getWindow();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Time is done,\n" +
                        "try again");
                alert.show();
                //    ButtonType result = alert.getResult();
                // if (result.equals(ButtonType.OK)) {
                // if(viewModel.getCharacterPositionRow()!=0 || viewModel.getCharacterPositionColumn()!=0)
                Timer();
                // }
            }
        }
    }


    public void updateLives() {
        String livesLeft = lives.get();
        if (livesLeft.equals("* * *"))
            lives.set("* *");
        else if (livesLeft.equals("* *"))
            lives.set("*");
        else if (livesLeft.equals("*")) {
            lives.set("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You lose the crown,\n" +
                    "try again next time");
            alert.show();
            time.stop();
            btn_GenerateMaze.setDisable(false);
        }
    }

    public void Timer() {
        time = new Timeline(new KeyFrame(Duration.seconds(1), evt -> updateTime()));
        time.setCycleCount(Animation.INDEFINITE); // repeat over and over again
        timeSeconds.set(startTime);
        time.play();
    }


    public void Mute(ActionEvent actionEvent) {
        if (btn_StopMusic.getText().equals("Music")) {
            setMusic(true);
        } else {
            setMusic(false);
        }
    }

    private void setMusic(boolean musicOn) {
        if (musicOn) {
            this.mediaPlayer.play();
            btn_StopMusic.setText("Mute");
        } else {
            this.mediaPlayer.stop();
            btn_StopMusic.setText("Music");

        }
    }

    public void mouseDrag(MouseEvent k) {
        if (!showOnce) {
            if (k.isDragDetect()) {
                viewModel.moveCharacter(k, mazeDisplay);
                k.consume();
            }
        }
    }

    public void scroll(ScrollEvent event) {
        viewModel.scroll(event, mazeDisplay);
    }

    public void cbCharacter(ActionEvent actionEvent)  {
        if (cbBCharacter.getValue().equals("JonSnow"))
            mazeDisplay.changeImages("JonSnow");
        else if(cbBCharacter.getValue().equals("Daenerys"))
            mazeDisplay.changeImages("Daenerys");
        else
            mazeDisplay.changeImages("CerseiLannister");
        btn_GenerateMaze.setVisible(true);
    }

}
