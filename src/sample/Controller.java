package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
         * Also mark the GridPane cells as valid targets for a drag n' drop motion.
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
                child.setOnDragOver(new EventHandler <DragEvent>() {
                    public void handle(DragEvent event) {
                        /* data is dragged over the target */
                        System.out.println("onDragOver");

                        /* accept it only if it is  not dragged from the same node
                         * and if it has a string data */
                        if (event.getGestureSource() != child &&
                                event.getDragboard().hasString()) {
                            /* allow for both copying and moving, whatever user chooses */
                            event.acceptTransferModes(TransferMode.MOVE);
                        }

                        event.consume();
                    }
                });
                child.setOnDragEntered(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        /* the drag-and-drop gesture entered the target */
                        /* show to the user that it is an actual gesture target */
                        if (event.getGestureSource() != child &&
                                event.getDragboard().hasString()) {
                            child.setStyle("-fx-border-color: darkblue; -fx-border-width: 3;");
                        }

                        event.consume();
                    }
                });
                child.setOnDragExited(new EventHandler <DragEvent>() {
                    public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                        child.setStyle("-fx-border-width: 0;");
                        event.consume();
                    }
                });

                //TODO What should the board do when it receives a tile? Rigorously define the procedure
                child.setOnDragDropped(new EventHandler <DragEvent>() {
                    public void handle(DragEvent event) {
                        /* data dropped */
                        System.out.println("onDragDropped");
                        /* if there is a string data on dragboard, read it and use it */
                        Dragboard db = event.getDragboard();
                        boolean success = false;
                        if (db.hasString()) {
                            // Creates the text element in the view model at that position if it doesn't exist.
                            if (viewModel[row][col] == null)
                            {
                                viewModel[row][col] = new Text(db.getString());
                                ((StackPane)child).getChildren().add(viewModel[row][col]);
                            }
                            viewModel[row][col].setText(db.getString());

                            success = true;
                        }
                        /* let the source know whether the string was successfully
                         * transferred and used */
                        event.setDropCompleted(success);

                        event.consume();
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
            final int ii = i;
            StackPane s = new StackPane();
            s.setStyle("-fx-background-color: lightyellow");
            s.setMinWidth(40);
            s.setMinHeight(40);
            s.setMaxWidth(40);
            s.setMaxHeight(40);
            s.getChildren().add(new Text("" + playerHand[ii]));
            playerHandHBox.getChildren().add(s);

            // Mark the user's tiles as valid sources for a drag n' drop motion.
            s.setOnDragDetected(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/
                    System.out.println("onDragDetected");

                /* allow any transfer mode */
                    Dragboard db = s.startDragAndDrop(TransferMode.MOVE);

                /* put a string on dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString("" + playerHand[ii]);
                    db.setContent(content);

                    event.consume();
                }
            });

            s.setOnDragDone(new EventHandler <DragEvent>() {
                public void handle(DragEvent event) {
                /* the drag-and-drop gesture ended */
                    System.out.println("onDragDone");
                /* if the data was successfully moved, clear it */
                    if (event.getTransferMode() == TransferMode.MOVE) {
                        playerHandHBox.getChildren().remove(s);
                    }

                    event.consume();
                }
            });
        }
    }
}
