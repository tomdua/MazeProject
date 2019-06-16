package View;

import algorithms.mazeGenerators.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
/**
 * MazeDisplay class extends Canvas
 *
 */
public class MazeDisplay extends Canvas {
    private int[][] maze;
    private int[][] solved;
    private int characterPositionRow;
    private int characterPositionColumn;
    private boolean isSolved;
    private Position endPosition;
    private String homePath = "";
    private String characterPath = "";
    private String wallPath = "";
    GraphicsContext graphics = getGraphicsContext2D();

    public int[][] getMaze() {
        return maze;
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public void endPosition(algorithms.mazeGenerators.Position end) {
        endPosition = end;
    }

    public void Solved(int[][] answer) {
        solved = answer;
    }

    public String getCharacterPath() {
        return characterPath;
    }

    public void setCharacterPath(String characterPath) {
        this.characterPath = characterPath;
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public String getWallPath() {return wallPath;}

    public void setWallPath(String wallPath) {
        this.wallPath = wallPath;
    }

    public void setGoalPosition(Position goalPosition) {
        this.endPosition = goalPosition;
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
    }

    public void isSolved(boolean solved) {
        this.isSolved = solved;
    }

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getWidth();
            double canvasWidth = getHeight();
            double cellHeight = canvasHeight / maze[0].length;
            double cellWidth = canvasWidth / maze.length;

            try {
                // Photo of StartPoint:
                graphics.clearRect(0, 0, getWidth(), getHeight()); //Clears the canvas
                Image StartPoint = new Image(new FileInputStream(getHomePath()));
                graphics.drawImage(StartPoint, 0, 0, cellHeight, cellWidth);
                // Photo of wallImage:
                Image wallImage = new Image(new FileInputStream(getWallPath()));

                // Photo of endGame:
                Image endPos = new Image(new FileInputStream("resources/images/end1.png"));
                graphics.drawImage(endPos, endPosition.getColumnIndex() * cellHeight, endPosition.getRowIndex() * cellWidth, cellHeight, cellWidth);

                //set maze on the screen
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            graphics.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                        }
                    }
                }
                if (isSolved) {
                    Image SolutionImage = new Image(new FileInputStream("resources/images/solve.png"));
                    for (int i = 0; i < solved[0].length - 1; i++) {
                        int x = solved[0][i];
                        int y = solved[1][i];
                        graphics.drawImage(SolutionImage, y * cellHeight, x * cellWidth, cellHeight, cellWidth);

                    }
                }
                // Photo of character:
                Image characterImage = new Image(new FileInputStream(getCharacterPath()));
                graphics.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(String.format("Image not exist: %s", e.getMessage()));
                alert.show();
            }
        }
    }
    // change the home,wall and character images.
    public void changeImages(String character) {
        if (character.equals("JonSnow")) {
            setCharacterPath("resources/images/JonSnow.png");
            setHomePath("resources/images/JonSnowHome.png");
            setWallPath("resources/images/wallJonSnow.jpg");
        } else if (character.equals(("Daenerys"))) {
            setCharacterPath("resources/images/Daenerys.png");
            setHomePath("resources/images/DaenerysHome.png");
            setWallPath("resources/images/wallDaenerys.jpg");
        } else if (character.equals(("CerseiLannister"))) {
            setCharacterPath("resources/images/CerseiLannister.png");
            setHomePath("resources/images/LannisterHome.png");
            setWallPath("resources/images/wallCerseiLannister.jpg");
        }
    }

}
