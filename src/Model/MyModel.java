package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * MyModel class extends Observable implements IModel
 * Responsible for loading the solution and the maze
 * Works with the server and the client.
 * Update maze details.
 * Responsible for keyboard control.
 */


public class MyModel extends Observable implements IModel {
    private int[][] maze;
    private Maze myMaze;
    private boolean solved;
    private boolean gameFinish;
    private boolean isAtTheEnd;
    private int characterPositionRow;
    private int characterPositionColumn;
    private Position endPosition;
    private int[][] mazeSolutionArr;
    private Server serverMazeGenerator;
    private Server serverSolveMaze;
    Client client;
    ExecutorService threadPool;

    public MyModel() {
        this.threadPool = Executors.newFixedThreadPool(15);
        isAtTheEnd = false;
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }
    public void setCharacterPositionRow(int row) {
        this.characterPositionRow = row;
    }
    public int getCharacterPositionColumn() { return characterPositionColumn; }
    public void setCharacterPositionCol(int col) {
        this.characterPositionColumn = col;
    }
    public Position getEndPosition() {
        return endPosition;
    }
    @Override
    public boolean gameFinish() {
        return gameFinish;
    }


    private void mazeToArr(Maze maze) {
        int row = maze.numOfRows();
        int col = maze.numOfColumns();
        this.maze = new int[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                this.maze[i][j] = maze.getCellValue(i, j);
    }
//Get size of maze, create maze and than compress and sent to client
    @Override
    public int[][] generateMaze(int width, int height) {
        serverMazeGenerator = new Server(5400, 1000, new ServerStrategyGenerateMaze()
        );
        serverMazeGenerator.start();
        try {
            client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        solved = false;
                        gameFinish = false;
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject();
                        InputStream inputStream = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[mazeDimensions[0] * mazeDimensions[1] + 8 ];
                        inputStream.read(decompressedMaze);
                        Maze maze = new Maze(decompressedMaze);
                        Position UpdatePos;
                        UpdatePos = maze.getStartPosition();
                        myMaze = maze;
                        characterPositionColumn = UpdatePos.getColumnIndex();
                        characterPositionRow = UpdatePos.getRowIndex();
                        endPosition = maze.getGoalPosition();
                        mazeToArr(maze);
                    //  Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverMazeGenerator.stop();
        isAtTheEnd = false;
        //threadPool.shutdownNow();
        setChanged();
        notifyObservers();
        return maze;
    }

// Read the maze from the client, and get the solution if excite from temp dir
    @Override
    public void generateSolution(MyViewModel m, int charRow, int charCol, String str) {
        serverSolveMaze = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        serverSolveMaze.start();
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = myMaze;
                        maze.setStartPosition(new Position(charRow,charCol));
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject();
                        solved = true;
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        int sizeOfSolution = mazeSolutionSteps.size();
                        mazeSolutionArr = new int[2][sizeOfSolution];
                        if(str.equals("solve")) {
                            for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                                mazeSolutionArr[0][i] = ((MazeState) (mazeSolutionSteps.get(i))).getRow();
                                mazeSolutionArr[1][i] = ((MazeState) (mazeSolutionSteps.get(i))).getCol();
                            }
                        }
                        setChanged();
                        notifyObservers();
              //          Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverSolveMaze.stop();
    }

//Check if is legal move
    private boolean isNotLegalMove(int x, int y) {
        if (x < 0 || y < 0 || x > maze.length - 1 || y > maze[0].length - 1)
            return true;
        return maze[x][y] == 1;
    }
//control : keyboard
    @Override
    public void moveCharacter(KeyCode movement) {
        int x = characterPositionRow;
        int y = characterPositionColumn;
        switch (movement) {
            case UP:
            case NUMPAD8:
            case W:
                if (!isNotLegalMove(x - 1, y))
                    characterPositionRow--;
                break;
            case DOWN:
            case NUMPAD2:
            case S:
                if (!isNotLegalMove(x + 1, y))
                    characterPositionRow++;
                break;
            case RIGHT:
            case NUMPAD6:
            case D:
                if (!isNotLegalMove(x, y + 1))
                    characterPositionColumn++;
                break;
            case LEFT:
            case A:
            case NUMPAD4:
                if (!isNotLegalMove(x, y - 1))
                    characterPositionColumn--;
                break;
            case NUMPAD3:
            case C:
                if (!isNotLegalMove(x + 1, y + 1))
                    if (!isNotLegalMove(x, y + 1) || !isNotLegalMove(x + 1, y)) {
                        characterPositionColumn++;
                        characterPositionRow++;
                    }
                break;
            case NUMPAD1:
            case Z:
                if (!isNotLegalMove(x + 1, y - 1))
                    if (!isNotLegalMove(x, y - 1) || !isNotLegalMove(x + 1, y)) {
                        characterPositionColumn--;
                        characterPositionRow++;
                    }
                break;
            case NUMPAD9:
            case E:
                if (!isNotLegalMove(x - 1, y + 1))
                    if (!isNotLegalMove(x - 1, y) || !isNotLegalMove(x, y + 1)) {
                        characterPositionColumn++;
                        characterPositionRow--;
                    }
                break;
            case NUMPAD7:
            case Q:
                if (!isNotLegalMove(x - 1, y - 1))
                    if (!isNotLegalMove(x, y - 1) || !isNotLegalMove(x - 1, y)) {
                        characterPositionColumn--;
                        characterPositionRow--;
                    }
                break;
        }
        if (endPosition.getColumnIndex() == getCharacterPositionColumn() && endPosition.getRowIndex() == getCharacterPositionRow()){
            gameFinish = true;
        isAtTheEnd = false;
        }
        setChanged();
        notifyObservers();

    }

    @Override
    public int[][] getMaze() {
        return maze;
    }


    @Override
    public void setGoalPosition(Position goalPosition) {
        this.endPosition = goalPosition;
    }

    @Override
    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public void setMazeOriginal(Maze m){
        this.myMaze =m;
        mazeToArr(m);
    }

    public boolean isSolved() {
        return this.solved;
    }

    public int[][] getMazeSolutionArr() {
        return mazeSolutionArr;
    }

    @Override
    public boolean isAtTheEnd() { return isAtTheEnd; }

//Load file (maze)
    public void load(File file)
    {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Maze mazeTemp;
            mazeTemp = (Maze)objectInputStream.readObject();
            setMazeOriginal(mazeTemp);
            setGoalPosition(mazeTemp.getGoalPosition());
            setCharacterPositionRow(mazeTemp.getStartPosition().getRowIndex());
            setCharacterPositionCol(mazeTemp.getStartPosition().getColumnIndex());
            solved=false;
            isAtTheEnd = false;
            objectInputStream.close();
            setChanged();
            notifyObservers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//Save file.
    public void save(File file)
    {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            solved = false;
            myMaze.setStartPosition(new Position(characterPositionRow,characterPositionColumn));
            objectOutputStream.writeObject(myMaze);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
         e.printStackTrace();
        }
    }
}
