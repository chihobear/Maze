
/**
 * Drawing reads a file in that is hard coded in start and
 * turns that file's character representation of a maze
 * into a drawing on a JavaFX canvas.
 * 
 * Adapted from the Section 10 code from CS210 Fall 2018.
 * 
 */
import java.io.File;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Drawing extends Application {
    // GUI objects
    private Button nextMove;
    private TextField command;
    private GraphicsContext gc;

    private int x;
    private int y;
    private final int MOVE = 1;
    private char[][] maze2;

    // 25 x 25 pixle square
    final int SIZE = 25;
    // Points needed to draw a triangle
    final int TRIANGLE = 3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Process the input file and store it in a 2D array of characters
        this.maze2 = readMaze("PublicTestCases/maze_02.txt");
        primaryStage.setTitle("A-maze-ing!");

        // Group root = new Group();

        // Initialized canvas to a default size for demo
        int DEMO = 15;

        // TODO: parameterize so that the canvas is initizlied to the length and
        // width of the maze
        Canvas canvas = new Canvas(DEMO * SIZE, DEMO * SIZE);

        gc = canvas.getGraphicsContext2D();
        // root.getChildren().add(canvas);


        // Text field to take in maze command
        command = new TextField();

        // Border pane will contain canvas for drawing and text area underneath
        BorderPane p = new BorderPane();

        // Input Area + nextMove Button
        Label cmd = new Label("Type Command in TextField");
        HBox hb = new HBox(3);

        // Input + Text Output
        VBox vb = new VBox(2);

        setupNodes(hb, cmd, vb);
        setupEventHandlers();

        p.setCenter(canvas);
        p.setBottom(vb);

        initScreenMaze(gc, this.maze2);

        // primaryStage.setScene(new Scene(root));
        primaryStage.setScene(new Scene(p));
        primaryStage.show();
    }

    /*
     * TODO: Iterate over the 2D array maze and draw the shape that corresponds
     * to the character. You will have to parameterize the draw shape method
     * calls below. Nested loops are acceptable.
     */
    public void initScreenMaze(GraphicsContext gc, char[][] maze) {
        // default is yellow because it is a corn maze
        gc.setFill(Color.YELLOW);

        // Demo starting points
        int S_POINT = 0; // Start location for square
        int T_POINT = 2; // Start location for triangle
        int S_POINT1 = 0;
        int T_POINT1 = 2;

        // Example square drawn
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] == '*') {
                    gc.fillRect(j * SIZE, i * SIZE, SIZE, SIZE);
                }
                if (maze[i][j] == 'S') {
                    T_POINT = i;
                    S_POINT = j;
                    x = j;
                    y = i;
                }
                if (maze[i][j] == 'E') {
                    T_POINT1 = i;
                    S_POINT1 = j;
                }
            }
        }

        // Example triangle drawn
        gc.setFill(Color.BLUE);
        // Two arrays that correspond to the points of the triangle to be drawn
        double[] yPoints = new double[] {
                (double) (T_POINT * SIZE + SIZE), (double) SIZE * T_POINT,
                (double) (SIZE * T_POINT + SIZE) };
        double[] xPoints = new double[] { (double) S_POINT * SIZE,
                (double) (SIZE * S_POINT + (SIZE / 2)),
                (double) (SIZE * S_POINT + SIZE) };
        // Could just pass arrays straight into fillPolygon
        gc.fillPolygon(xPoints, yPoints, TRIANGLE);
        yPoints = new double[] { (double) (T_POINT1 * SIZE + SIZE),
                (double) SIZE * T_POINT1, (double) (SIZE * T_POINT1 + SIZE) };
        xPoints = new double[] { (double) S_POINT1 * SIZE,
                (double) (SIZE * S_POINT1 + (SIZE / 2)),
                (double) (SIZE * S_POINT1 + SIZE) };
        gc.fillPolygon(xPoints, yPoints, TRIANGLE);

    }

    /*
     * readMaze accepts a string arguement that is the file name
     * 
     * TODO: Process the file and place its contents in the 2D
     * array Maze. This 2D array will be returned so that you
     * are able to iterate over it when drawing on the Canvas.
     */
    public char[][] readMaze(String fileName) {
        char[][] maze = null;
        Scanner input = null;
        try {
            input = new Scanner(new File(fileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String[] size = input.nextLine().trim().split(" ");
        maze = new char[Integer.valueOf(size[0])][Integer.valueOf(size[1])];
        for (int i = 0; i < Integer.valueOf(size[0]); i++) {
            String line = input.nextLine();
            for (int j = 0; j < Integer.valueOf(size[1]); j++) {
                maze[i][j] = line.substring(j, j + 1).toCharArray()[0];
            }
        }
        return maze;
    }

    /*
     * Sets up the TextField, label, and button to be
     * in the bottom
     */
    private void setupNodes(HBox hb, Label cmd, VBox vb) {

        nextMove = new Button("Simulation Step");

        hb.setSpacing(15);
        hb.getChildren().add(cmd);
        hb.getChildren().add(nextMove);

        vb.getChildren().add(hb);
        vb.getChildren().add(command);
    }

    /*
     * Button EventHandler to take input command
     * when button is clicked.
     */
    private void setupEventHandlers() {
        nextMove.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                parseLine(command.getText());
            }
        });
    }

    public void parseLine(String line) {
        System.out.println("reading " + line); // Print line for debugging
        if (line.toLowerCase().compareTo("up") == 0 && y - MOVE >= 0
                && maze2[y - MOVE][x] != '*') {
            move(y - MOVE, x);
        }
        if (line.toLowerCase().compareTo("right") == 0 && x + MOVE < SIZE
                && maze2[y][x + MOVE] != '*') {
            move(y, x + MOVE);
        }
        if (line.toLowerCase().compareTo("left") == 0 && x - MOVE >= 0
                && maze2[y][x - MOVE] != '*') {
            move(y, x - MOVE);
        }
        if (line.toLowerCase().compareTo("down") == 0 && y + MOVE < SIZE
                && maze2[y + MOVE][x] != '*') {
            move(y + MOVE, x);
        }
    }

    /**
     * This function 'erases' the old triangle and redraws
     * it in the next move location to make it appear that
     * the triangle is navigating the maze. int newY and
     * int newX represent the coordinates of the valid move.
     */
    public void move(int newY, int newX) {
        gc.clearRect(x * SIZE, y * SIZE, SIZE, SIZE);
        double[] yPoints = new double[] { (double) (newY * SIZE + SIZE),
                (double) SIZE * newY, (double) (SIZE * newY + SIZE) };
        double[] xPoints = new double[] { (double) newX * SIZE,
                (double) (SIZE * newX + (SIZE / 2)),
                (double) (SIZE * newX + SIZE) };
        gc.setFill(Color.BLUE);
        gc.fillPolygon(xPoints, yPoints, TRIANGLE);
        x = newX;
        y = newY;
    }

}
