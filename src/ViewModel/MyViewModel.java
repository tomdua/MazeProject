package ViewModel;

import Model.IModel;
import View.MazeDisplay;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    public StringProperty characterPositionRow = new SimpleStringProperty("Row");
    public StringProperty characterPositionColumn = new SimpleStringProperty("Col");
    private IModel model;
    private int characterPositionRowIndex = 0;
    private int characterPositionColumnIndex = 0;

    public MyViewModel(IModel model) {
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers();
        }
    }

    public int[][] generateMaze(int width, int height) {
        return model.generateMaze(width, height);
    }

    public Position getEndPosition() { return model.getEndPosition(); }

    public void moveCharacter(KeyCode movement) { model.moveCharacter(movement); }

    public boolean gameFinish() {
        return model.gameFinish();
    }

    public int[][] getMaze() { return model.getMaze(); }

    public void setMaze(int[][] maze) {
        model.setMaze(maze);
    }

    public int getCharacterPositionRow() { return characterPositionRowIndex; }

    public void setCharacterPositionRow(int raw) { model.setCharacterPositionRow(raw); }

    public int getCharacterPositionColumn() { return characterPositionColumnIndex; }

    public void setCharacterPositionColumn(int column) {
        model.setCharacterPositionCol(column);
    }

    public void getSolution(MyViewModel m, int charRow, int charCol, String x) { model.generateSolution(m, charRow, charCol, x); }

    public boolean isSolved() {
        return model.isSolved();
    }

    public int[][] getMazeSolutionArr() {
        return model.getMazeSolutionArr();
    }

    public void save(File file) {
        model.save(file);
    }

    public void load(File file) {
        model.load(file);
    }

}
