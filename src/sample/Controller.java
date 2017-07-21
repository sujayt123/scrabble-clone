package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    /* Access to the GUI representation of the board. Useful for defining drag-and-drop events. */
    private StackPane[][] board_cells = new StackPane[15][15];

    /* Access to the GUI GridPane.*/
    @FXML
    private GridPane gridPane;

    @FXML
    private HBox playerHandHBox;

    /* The data currently represented on the screen. Propagates to main model at certain points in gameplay.
     * Bound to the text stored in each board_cell, if it exists. Any non-existing text states are created lazily.
     */
    private Text [][] viewModel = new Text[15][15];

    /* The most recently accepted state of the board. State may change upon successful user or computer move. */
    private char [][] mainModel = new char[15][15];

    private Queue<Character> tilesRemaining;

    private char[] playerHand, cpuHand;

    /*
     * Runs initialization routines right after the view loads.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
         * Initialize the bindings to the viewmodel (view's understanding of board) and the board cells housing them.
         */
        gridPane.getChildren().forEach((child)->{
            if (child instanceof StackPane)
            {
                final int row = gridPane.getRowIndex(child);
                final int col = gridPane.getColumnIndex(child);
                board_cells[row][col] = (StackPane) child;
                ((StackPane) child).getChildren().forEach((item_in_stackpane) -> {
                    if (item_in_stackpane instanceof Text) {
                        viewModel[row][col] = (Text) item_in_stackpane;
                    }
                });
            }
        });

        List<Character> tileList =
                Arrays.asList( 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E',
                        'E', 'E', 'A', 'A', 'A', 'A', 'A', 'A', 'A',
                        'A', 'A', 'I', 'I', 'I', 'I', 'I', 'I', 'I',
                        'I', 'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O',
                        'O', 'N', 'N', 'N', 'N', 'N', 'N', 'R', 'R',
                        'R', 'R', 'R', 'R', 'T', 'T', 'T', 'T', 'T',
                        'T', 'L', 'L', 'L', 'L', 'S', 'S', 'S', 'S',
                        'U', 'U', 'U', 'U', 'D', 'D', 'D', 'D', 'G',
                        'G', 'G', 'B', 'B', 'C', 'C', 'M', 'M', 'P',
                        'P', 'F', 'F', 'H', 'H', 'V', 'V', 'W', 'W',
                        'Y', 'Y', 'K', 'X', 'J', 'Q', 'Z');
        // Shuffle the tiles and arrange them into a queue.
        Collections.shuffle(tileList);
        tilesRemaining = new ArrayDeque<>(tileList);

        // Prepare each player to receive tiles.
        playerHand = new char[9];
        cpuHand = new char[9];

        // Distribute the starting racks (hereafter referenced as "hands") to the computer and the player.
        for (int i = 0; i < 7; i++)
        {
            playerHand[i] = tilesRemaining.remove();
            cpuHand[i] = tilesRemaining.remove();
        }

        // Display the player's hand as stackpanes in the HBox in the bottom of the borderpane layout.
        for (int i = 0; i < 7 ; i++)
        {
            StackPane s = new StackPane();
            s.setStyle("-fx-background-color: lightyellow");
            s.setMinWidth(40);
            s.setMinHeight(40);
            s.setMaxWidth(40);
            s.setMaxHeight(40);
            s.getChildren().add(new Text("" + playerHand[i]));
            playerHandHBox.getChildren().add(s);
        }
    }
}
