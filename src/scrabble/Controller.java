package scrabble;

import API.AI;
import API.Tile;
import API.Trie;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;
import util.FunctionHelper;
import util.Quadruple;

import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static API.Board.scoreMove;
import static API.Board.validMove;
import static API.Tile.getTileBagForGame;
import static util.FunctionHelper.*;

/**
 * The controller for the Scrabble Game.
 *
 * Created by sujay on 7/21/17
 */

public class Controller implements Initializable {

    /**
     *  Access to the GUI representation of the board. Useful for defining drag-and-drop events.
     */
    private StackPane[][] board_cells;

    /**
     * Access to the GUI GridPane.
     */
    @FXML
    private GridPane gridPane;

    /**
     * Access to the HBox containing the player hand.
     */
    @FXML
    private HBox playerHandHBox;

    /**
     * Access to the text region containing the player score.
     */
    @FXML
    private Text playerScore;

    /**
     * Access to the text region containing the CPU score.
     */
    @FXML
    private Text cpuScore;

    /**
     * The buttons in the layout.
     */
    @FXML
    private Button moveButton, passButton, recallButton, swapTilesButton;

    /**
     * Access to the text region containing the status message.
     */
    @FXML
    private Text statusMessage;

    /**
     * The data currently represented on the screen. Propagates to main model at certain points in gameplay.
     * Bound to the text stored in each board_cell, if it exists. Any non-existing text states are created lazily.
     */
    private List<List<Text>> viewModel;

    /**
     *  The most recently accepted state of the board. State may change upon successful user or computer move.
     *  Updated with a-z character values as board changes.
     */
    private List<List<Character>> mainModel;

    /**
     * A queue that represents the bag of tiles remaining.
     */
    private Queue<Character> tilesRemaining;

    /**
     * A list for the player and cpu racks (henceforth referenced as "hands").
     */
    private List<Character> playerHand, cpuHand;

    /**
     * A prefix tree data structure to house the dictionary of scrabble words. See "util" for more information.
     */
    private static Trie trie;

    /**
     * Flag to disallow placement of tiles on board while the user is swapping.
     */
    private boolean isSwapping;

    /**
     * Integer tracking number of zero scoring turns on each player's side.
     */
    private int cpuConsecutiveZeroScoringTurns, playerConsecutiveZeroScoringTurns;

    /**
     * A list of references to the elements of playerHandHBox that the user would like to swap.
     */
    private List<StackPane> elementsToSwap;

    /**
     * Initialization code that runs at application boot-time.
     * @param location (unused)
     * @param resources (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Read dictionary into trie. */
        if (trie == null)
        {
            try
            {
                trie = new Trie();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        board_cells = new StackPane[15][15];

        cpuConsecutiveZeroScoringTurns = 0;
        playerConsecutiveZeroScoringTurns = 0;
        isSwapping = false;

        /*
         * Generate the mainModel and viewModel Arraylists.
         */
        mainModel = forEachBoardSquareAsNestedList((r, c) -> ' ');
        viewModel = forEachBoardSquareAsNestedList((r, c) -> new Text(" "));
        elementsToSwap = new ArrayList<>();

        /*
         * Initialize the bindings to the view-model (view's understanding of board) data structure
         * and the containers (cells, more precisely, stack panes) housing it.
         * Mark these containers as valid targets for a drag n' drop motion.
         */
        gridPane.getChildren()
                .filtered(child -> child instanceof StackPane)
                .forEach(child -> {
                    final int row = gridPane.getRowIndex(child);
                    final int col = gridPane.getColumnIndex(child);
                    board_cells[row][col] = (StackPane) child;

                    // Make sure the view model is represented by the stack panes.
                    FilteredList<Node> l = ((StackPane) child).getChildren().
                            filtered((grandchild)-> grandchild instanceof Text);

                    if (l.isEmpty())
                    {
                        ((StackPane) child).getChildren().add(viewModel.get(row).get(col));
                    }
                    else
                    {
                        viewModel.get(row).set(col, (Text) l.get(0));
                    }

                    NumberBinding n = Bindings.min(gridPane.widthProperty().divide(15), gridPane.heightProperty().divide(15));
                    board_cells[row][col].prefWidthProperty().bind(n);
                    board_cells[row][col].prefHeightProperty().bind(n);
                    gridPane.prefWidthProperty().bind(((BorderPane)gridPane.getParent()).widthProperty());
                    gridPane.prefHeightProperty().bind(((BorderPane)gridPane.getParent()).heightProperty());

                    child.setOnDragOver((event) -> {
                        /* accept it only if it is  not dragged from the same node
                         * and if it has a string data. also, ensure that
                          * the board cell can actually receive this tile */
                        Text viewModelText = viewModel.get(row).get(col);
                        if (!isSwapping &&
                                event.getGestureSource() != child &&
                                event.getDragboard().hasString() &&
                                (viewModelText.getText().length() == 2 ||
                                        viewModelText.getText().charAt(0) == ' ')) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }

                        event.consume();
                    });

                    child.setOnDragEntered((event) -> {
                        /* the drag-and-drop gesture entered the target */
                        /* show to the user that it is an actual gesture target */
                        Text viewModelText = viewModel.get(row).get(col);
                        if (!isSwapping &&
                                event.getGestureSource() != child &&
                                event.getDragboard().hasString() &&
                                (viewModelText.getText().length() == 2 ||
                                        viewModelText.getText().charAt(0) == ' ')) {
                            child.setStyle("-fx-border-color: darkblue; -fx-border-width: 3;");
                        }

                        event.consume();
                    });

                    child.setOnDragExited((event) -> {
                        /* mouse moved away, remove the graphical cues */
                        child.setStyle("-fx-border-width: 0;");
                        event.consume();
                    });

                    //TODO What should the board do when it receives a tile? Rigorously define the procedure
                    child.setOnDragDropped((event) -> {
                        /* if there is a string data on dragboard, read it and use it */
                        Dragboard db = event.getDragboard();
                        boolean success = false;
                        if (db.hasString()) {
                            // Creates the text element in the view model at that position if it doesn't exist.

                            /*
                             * Change the text color of the pane to Black if needed.
                             * This is to ensure that special squares are distinct from played tiles.
                             */
                            viewModel.get(row).get(col).getStyleClass().add("black-text");

//                            System.out.println(db.getString());
                            viewModel.get(row).get(col).setText(db.getString());

                            // Remove from hand
                            playerHandHBox.getChildren().stream()
                                    .filter((c) ->
                                            ((Text)((StackPane)c).getChildren().get(0)).getText().equals(db.getString())
                                    ).findFirst().ifPresent((c) ->
                                    playerHandHBox.getChildren().remove(c)
                            );

                            success = true;
                            child.getStyleClass().add("played-tile");
                        }
                        /* let the source know whether the string was successfully
                         * transferred and used */
                        event.setDropCompleted(success);
                        event.consume();
                    });
                });

        // Prepare to distribute tiles to players.
        tilesRemaining = getTileBagForGame();
        playerHand = new ArrayList<>();
        cpuHand = new ArrayList<>();

        // Distribute the starting racks (hereafter referenced as "hands") to the computer and the player.
        IntStream.range(0, 7).forEach( i->{
            playerHand.add(tilesRemaining.poll());
            cpuHand.add(tilesRemaining.poll());
            // Display the player's hand as stackpanes in the HBox in the bottom of the borderpane layout.
            addTileToUserHand(playerHand.get(i));
        });
    }

    /**
     * Recall all tiles placed on the board this turn to the player's hand.
     */
    public void recallTiles()
    {
        List<List<Character>> textInViewModel = forEachBoardSquareAsNestedList((r, c) ->
                viewModel.get(r).get(c).getText().length() == 1 ? viewModel.get(r).get(c).getText().charAt(0) : ' ');

        List<Pair<Integer, Integer>> changed_coordinates = FunctionHelper.getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return mainModel.get(r).get(c) != textInViewModel.get(r).get(c);
        }).collect(Collectors.toList());

        forEachProvidedSquareAsList((i, j) -> {
            addTileToUserHand(viewModel.get(i).get(j).getText().charAt(0));
            board_cells[i][j].getStyleClass().removeAll("played-tile");

            // Reset the view model's text in accordance with whether it's a special tile.
            if (board_cells[i][j].getStyleClass().size() > 0)
            {
                String specialText = board_cells[i][j].getStyleClass().get(0).substring(0, 2);
                viewModel.get(i).get(j).setText(specialText);
                viewModel.get(i).get(j).getStyleClass().remove("black-text");
            }
            else
            {
                viewModel.get(i).get(j).setText(" ");
            }
            return null;
        }, changed_coordinates);
    }

    /**
     * Utility method to add a character to the user's hand in the GUI.
     * @param letter The character to add to the HBOX housing the user's hand
     */
    private void addTileToUserHand(char letter)
    {
        StackPane s = new StackPane();
        s.setStyle("-fx-background-color: lightyellow");
        s.setMinWidth(40);
        s.setMinHeight(40);
        s.setMaxWidth(40);
        s.setMaxHeight(40);
        s.getChildren().add(new Text(letter + ""));
        playerHandHBox.getChildren().add(s);

        s.setOnMouseClicked((e) -> {
            if (isSwapping)
            {
                // First, check if this tile is already in the to-swap list.
                if (elementsToSwap.contains(s))
                {
                    // If it does, remove it from that list.
                    elementsToSwap.remove(s);

                    s.getStyleClass().removeAll("selected-for-swap");

                    // If doing so sets the list size to 0, disable the ready button.
                    if (elementsToSwap.size() == 0)
                    {
                        swapTilesButton.setDisable(true);
                    }
                }
                else
                {
                    elementsToSwap.add(s);
                    swapTilesButton.setDisable(false);
                    s.getStyleClass().add("selected-for-swap");
                }
            }
        });

        // Mark the user's tiles as valid sources for a drag n' drop motion.
        s.setOnDragDetected((event) -> {
            /* drag was detected, start drag-and-drop gesture*/
            Dragboard db = s.startDragAndDrop(TransferMode.MOVE);

            /* put a string on dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString("" + letter);
            db.setContent(content);

            event.consume();
        });

        /* if the data was successfully moved, clear it */
        s.setOnDragDone(Event::consume);
    }

    /**
     * Attempts a player move. Triggered on click of "Move" button in GUI.
     */
    public void attemptPlayerMove()
    {
        List<List<Character>> textInViewModel = forEachBoardSquareAsNestedList((r, c) ->
                viewModel.get(r).get(c).getText().length() == 1 ? viewModel.get(r).get(c).getText().charAt(0) : ' ');

        if (validMove(mainModel, textInViewModel, trie))
        {
            statusMessage.setText("Your move has been registered.");
            statusMessage.getStyleClass().clear();
            statusMessage.getStyleClass().add("success-text");
            makePlayerMove();
        }
        else
        {
            statusMessage.setText("Not a valid play.");
            statusMessage.getStyleClass().clear();
            statusMessage.getStyleClass().add("error-text");
        }
    }

    /**
     * Finalizes the move for the player, assuming it was valid. Propagates changes
     * from viewmodel to model.
     */
    private void makePlayerMove()
    {

        List<List<Character>> textInViewModel = forEachBoardSquareAsNestedList((r, c) ->
                viewModel.get(r).get(c).getText().length() == 1 ? viewModel.get(r).get(c).getText().charAt(0) : ' ');

        List<Pair<Integer, Integer>> changed_coordinates = FunctionHelper.getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return mainModel.get(r).get(c) != textInViewModel.get(r).get(c);
        }).collect(Collectors.toList());

        int incrementInScore = scoreMove(mainModel, textInViewModel);

        int score = Integer.parseInt(playerScore.getText().split(":")[1]) + incrementInScore;
        playerScore.setText("Player Score:" + score);

        // Step 2: propagate viewModel to model
        mainModel = forEachBoardSquareAsNestedList((r, c) ->
                viewModel.get(r).get(c).getText().length() == 1 ? viewModel.get(r).get(c).getText().charAt(0) : ' '
        );

        // Step 3: take as many tiles from the bag as you can (up to the number removed) and give them to the player
        forEachProvidedSquareAsList( (row, col) -> {
            playerHand.remove((Character)viewModel.get(row).get(col).getText().charAt(0));
            return tilesRemaining.poll();
        }, changed_coordinates).forEach((tile) -> {
            if (tile != null)
            {
                playerHand.add(tile);
                addTileToUserHand(tile);
            }
        });

        if (!gameOver(playerHand, playerConsecutiveZeroScoringTurns))
        {
            makeCPUMove();
        }
        else
        {
            cleanup();
        }

    }

    private void makeCPUMove()
    {
        statusMessage.getStyleClass().clear();
        statusMessage.getStyleClass().add("success-text");

        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>>
                cpuPlay = AI.CPUMove(new Quadruple<>(mainModel, cpuHand, tilesRemaining, trie));

        List<Pair<Integer, Integer>> changed_coordinates = FunctionHelper.getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return mainModel.get(r).get(c) != cpuPlay.getA().get(r).get(c);
        }).collect(Collectors.toList());

        cpuHand = cpuPlay.getB();
        tilesRemaining = cpuPlay.getC();
        String cpuPlayedString = cpuPlay.getD().getKey();
        int incrementInScore = cpuPlay.getD().getValue();

        boolean cpuSwapped = (incrementInScore == 0 && tilesRemaining.size() >= 7);

        // Reset the default colors of text on the board
        forEachBoardSquareAsList((r, c) -> {
            if (mainModel.get(r).get(c) != ' ')
            {
                viewModel.get(r).get(c).getStyleClass().removeAll("bold-text");
                viewModel.get(r).get(c).getStyleClass().add("black-text");
            }
            return null;
        });

        if (incrementInScore != 0)
        {
            mainModel = cpuPlay.getA();
            cpuConsecutiveZeroScoringTurns = 0;
            forEachProvidedSquareAsList((r, c) -> {
                // Side effects on the View Model

                viewModel.get(r).get(c).setText(mainModel.get(r).get(c) + "");
                viewModel.get(r).get(c).getStyleClass().add("bold-text");
                board_cells[r][c].getStyleClass().add("played-tile");
                cpuHand.remove((Character)mainModel.get(r).get(c));
                return null;
            }, changed_coordinates);
        }
        else if (cpuSwapped)
        {
            cpuConsecutiveZeroScoringTurns++;
            statusMessage.setText("CPU swapped some tiles.");
            if (gameOver(cpuHand, cpuConsecutiveZeroScoringTurns))
            {
                cleanup();
            }
            return;
        }
        else
        {
            cpuConsecutiveZeroScoringTurns++;
            statusMessage.setText("CPU passed the turn.");
            if (gameOver(cpuHand, cpuConsecutiveZeroScoringTurns))
            {
                cleanup();
            }
            return;
        }


        int score = Integer.parseInt(cpuScore.getText().split(":")[1]) + incrementInScore;
        cpuScore.setText("CPU Score:" + score);
        statusMessage.setText("CPU played " + cpuPlayedString + " for " + incrementInScore + " points.");
        if (gameOver(cpuHand, cpuConsecutiveZeroScoringTurns))
        {
            cleanup();
        }
    }


    public void passTurn()
    {
        playerConsecutiveZeroScoringTurns++;
        recallTiles();
        if (!gameOver(playerHand, playerConsecutiveZeroScoringTurns))
        {
            disablePlayerActions();
            makeCPUMove();
            enablePlayerActions();
        }
        else
        {
            cleanup();
        }
    }

    public void attemptSwap()
    {
        if (tilesRemaining.size() < 7)
        {
            statusMessage.setText("Cannot swap when fewer than 7 tiles remaining in bag.");
            statusMessage.getStyleClass().clear();
            statusMessage.getStyleClass().add("error-text");
            return;
        }
        isSwapping = true;
        disablePlayerActions();
        recallTiles();
        statusMessage.setText("Select the tiles to swap and hit ready, or hit back.");
        swapTilesButton.setText("Ready");
        recallButton.setText("Back");
        recallButton.setOnAction((ev) -> {
            statusMessage.setText("");
            recallButton.setText("Recall");
            swapTilesButton.setText("Swap Tiles");
            isSwapping = false;
            elementsToSwap.forEach(s -> {
                s.getStyleClass().removeAll("selected-for-swap");
            });
            elementsToSwap.clear();
            enablePlayerActions();
            swapTilesButton.setOnAction((e) -> attemptSwap());
            recallButton.setOnAction((e) -> recallTiles());
        });
        swapTilesButton.setOnAction((ev) -> {
            // Add new tiles from bag to player hand, prepare to add tiles discarded from player hand back to bag.
            List<Character> returnToBag = elementsToSwap.stream().map((s) -> {
                playerHandHBox.getChildren().remove(s);
                char toRemove = (Character)(((Text)s.getChildren().get(0)).getText().charAt(0));
                playerHand.remove((Character)toRemove);
                char toAdd = tilesRemaining.poll();
                playerHand.add(toAdd);
                addTileToUserHand(toAdd);
                return toRemove;
            }).collect(Collectors.toList());
            tilesRemaining.addAll(returnToBag);
            List<Character> shuffledTiles = new ArrayList<>(tilesRemaining);
            Collections.shuffle(shuffledTiles);
            tilesRemaining.clear();
            tilesRemaining.addAll(shuffledTiles);
            elementsToSwap.clear();

            recallButton.setText("Recall");
            swapTilesButton.setText("Swap Tiles");
            isSwapping = false;
            swapTilesButton.setOnAction((e) -> attemptSwap());
            recallButton.setOnAction((e) -> recallTiles());

            playerConsecutiveZeroScoringTurns++;
            if (!gameOver(playerHand, playerConsecutiveZeroScoringTurns))
            {
                makeCPUMove();
                enablePlayerActions();
            }
            else
            {
                cleanup();
            }
        });
    }

    private void disablePlayerActions()
    {
        moveButton.setDisable(true);
        passButton.setDisable(true);
        swapTilesButton.setDisable(true);
        if (!isSwapping)
        {
            recallButton.setDisable(true);
        }
    }

    private void enablePlayerActions()
    {
        moveButton.setDisable(false);
        passButton.setDisable(false);
        swapTilesButton.setDisable(false);
        recallButton.setDisable(false);
    }

    private boolean gameOver(List<Character> hand, int consecTurns)
    {
        return (hand.isEmpty()) || consecTurns == 3;

    }

    private void cleanup()
    {
        statusMessage.setText("Game over.");
        boolean normalEnding = playerHand.isEmpty() || cpuHand.isEmpty();
        int pScore = Integer.parseInt(this.playerScore.getText().split(":")[1]);
        int aiScore = Integer.parseInt(this.cpuScore.getText().split(":")[1]);
        // When the game is finished,
        if (normalEnding)
        {
            if (playerHand.isEmpty())
            {
                pScore += 2 * cpuHand.stream().map(Tile::scoreCharacter).reduce((a, b) -> a + b).get();
            }
            else
            {
                aiScore += 2 * playerHand.stream().map(Tile::scoreCharacter).reduce((a, b) -> a + b).get();
            }
        }
        playerScore.setText("Player Score:" + pScore);
        cpuScore.setText("CPU Score:" + aiScore);
        int pointDifferential = Math.abs(pScore - aiScore);
        if (pScore > aiScore)
        {
            statusMessage.getStyleClass().removeAll();
            statusMessage.getStyleClass().add("success-text");
            statusMessage.setText("Congratulations! You won by " + pointDifferential + " points.");
        }
        else if (pScore == aiScore)
        {
            statusMessage.getStyleClass().removeAll();
            statusMessage.getStyleClass().add("success-text");
            statusMessage.setText("Tie game!");
        }
        else
        {
            statusMessage.getStyleClass().removeAll();
            statusMessage.getStyleClass().add("error-text");
            statusMessage.setText("Oh no! You lost by " + pointDifferential + " points.");
        }
        disablePlayerActions();
        swapTilesButton.setDisable(false);
        swapTilesButton.setText("Reset");
        swapTilesButton.setOnAction((e) -> {
            newGame();
        });
    }

    /**
     * Resets the scrabble board.
     */
    private void clearBoard()
    {
        forEachBoardSquareAsList((r, c) -> {
            // Clear all styles from the text.
            viewModel.get(r).get(c).getStyleClass().removeAll(viewModel.get(r).get(c).getStyleClass().filtered(x->!x.contains("special")));
            // Clear all styles from the stackpane EXCEPT for special tile classes.
            board_cells[r][c].getStyleClass().removeAll(
                    board_cells[r][c].getStyleClass().filtered(x -> !(x.contains("DW") || x.contains("TW") || x.contains("DL") || x.contains("TL")))
            );
            // Clear the actual text for all non-special tiles.
            if (board_cells[r][c].getStyleClass().isEmpty())
                viewModel.get(r).get(c).setText(" ");
                // Replace the special tile text for the special tiles.
            else if (board_cells[r][c].getStyleClass().get(0).contains("DW"))
                viewModel.get(r).get(c).setText("DW");
            else if (board_cells[r][c].getStyleClass().get(0).contains("TW"))
                viewModel.get(r).get(c).setText("TW");
            else if (board_cells[r][c].getStyleClass().get(0).contains("DL"))
                viewModel.get(r).get(c).setText("DL");
            else
                viewModel.get(r).get(c).setText("TL");
            return null;
        });

        playerHandHBox.getChildren().clear();
        playerScore.setText("Player Score:0");
        cpuScore.setText("CPU Score:0");
        statusMessage.setText("");
        statusMessage.getStyleClass().clear();
    }

    public void showHelpMenu() throws Exception
    {
        new HelpBox().display();
    }

    public void newGame()
    {
        clearBoard();
        initialize(null, null);
        swapTilesButton.setText("Swap Tiles");
        swapTilesButton.setOnAction((e) -> attemptSwap());
        enablePlayerActions();
    }
}