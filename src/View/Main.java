package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Main extends Application {
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        primaryStage.setTitle("Game Of Thrones Maze");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add("Main.css");
        primaryStage.getIcons().add(new Image("file:resources/images/icon.png"));
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(600);
        MyViewController view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        primaryStage.setScene(scene);
        SetStageCloseEvent(primaryStage);
        primaryStage.show();
    }

    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to leave the game?\n"
                        + "Don't miss the chance to be the\n"
                        + "King of the seven kingdom!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    primaryStage.close();
                    Platform.exit();
                } else {
                    windowEvent.consume();
                }
            }
        });
    }
}
