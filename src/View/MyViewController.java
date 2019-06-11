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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    private static final int startTime = 2;
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
    public Button button;

    int mazeNum = 1;
    boolean showOnce = false;
    boolean songOnce = true;
    private Timeline time;
    private MediaPlayer mediaPlayer;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
        btn_GenerateMaze.setVisible(true);
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
            if (viewModel.gameFinish() && !showOnce) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Well Done,\n" +
                        "You'r the king of the seven kingdoms");
                Music(1);
                time.stop();
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


    public void mouseClicked() {
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

    public void solveMaze() {
        viewModel.getSolution(this.viewModel, this.viewModel.getCharacterPositionRow(), this.viewModel.getCharacterPositionColumn(), "solve");
    }


    public void exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to leave the game?\n"
                + "Don't miss the chance to be the\n"
                + "King of the seven kingdom!");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public void About() {
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

    public void Help() {
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
            System.out.println("Error miss file: Help.fxml");
        }
    }

    public void Option() {
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
            System.out.println("Error miss file: Option.fxml");
        }
    }

    public void saveGame() {
        FileChooser fc = new FileChooser();
        File filePath = new File("./GameOfThrones_Mazes/");
        if (!filePath.exists())
            filePath.mkdir();
        fc.setTitle("Saving maze");
        fc.setInitialFileName("GameOfThrones_MazeNumber" + mazeNum);
        mazeNum++;
        fc.setInitialDirectory(filePath);
        File file = fc.showSaveDialog(mazeDisplay.getScene().getWindow());
        if (file != null)
            viewModel.save(file);
    }

    public void loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Loading maze");
        File filePath = new File("./GameOfThrones_Mazes/");
        if (filePath.exists() != false)
            filePath.mkdir();
        fileChooser.setInitialDirectory(filePath);
        File file = fileChooser.showOpenDialog(new PopupWindow() {
        });
        if (file != null && file.exists() && !file.isDirectory()) {
            viewModel.load(file);
            if (songOnce == true)
                Music(0);
            mazeDisplay.redraw();
        }
    }

    //set music on
    public void Music(int on) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        String path;
        if (on == 0) {
            songOnce = false;
            path = "resources\\music\\start.mp3";
        } else {
            songOnce = true;
            path = "resources\\music\\end.mp3";
        }
        Media temp = new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(temp);
        mediaPlayer.play();
    }

    //Update time for timer, every time 2 min(120 seconds) for finish.
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
                time.stop();
                Stage stage = new Stage();
                stage.setTitle("Alert");
                button = new Button();
                button.setText("OK");
                button.setOnAction(event -> {
                    Timer();
                    Stage s = (Stage) button.getScene().getWindow();
                    s.close();
                });
                button.setLayoutX(115);
                button.setLayoutY(150);

                Pane layout = new Pane();
                layout.setPrefHeight(180);
                layout.setPrefWidth(260);
                layout.getChildren().add(button);

                Text t1 = new Text();
                t1.setText("Time is up!");
                t1.setFont(Font.font ("Verdana", 20));
                t1.setLayoutX(0);
                t1.setLayoutY(35);
                // t1.setFont(Font.font(System,19.5,));
                layout.getChildren().add(t1);

                Text t2 = new Text();
                t2.setText("Try again!\n" + "You can do it!");
                t2.setFont(Font.font ("Verdana", 12.5));
                t2.setLayoutY(79);
                t2.setLayoutX(14);


                layout.getChildren().add(t2);
                Scene scene = new Scene(layout, 260, 185);

                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            }
        }
    }


    //Update the life of the hero, when the time is over.
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

    //start music.
    private void setMusic(boolean musicOn) {
        if (musicOn) {
            this.mediaPlayer.play();
            btn_StopMusic.setText("Mute");
        } else {
            this.mediaPlayer.stop();
            btn_StopMusic.setText("Music");

        }
    }

    //set music on mute
    public void Mute() {
        if (btn_StopMusic.getText().equals("Music")) {
            setMusic(true);
        } else {
            setMusic(false);
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

    //button of change characters.
    public void cbCharacter() {
        if (cbBCharacter.getValue().equals("JonSnow"))
            mazeDisplay.changeImages("JonSnow");
        else if (cbBCharacter.getValue().equals("Daenerys"))
            mazeDisplay.changeImages("Daenerys");
        else
            mazeDisplay.changeImages("CerseiLannister");
        btn_GenerateMaze.setVisible(true);
    }


}
