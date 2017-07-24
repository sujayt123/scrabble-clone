package scrabble;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by sujay on 7/24/17.
 */
public class HelpBox implements Initializable {

    @FXML
    private Text titleText;

    @FXML
    private TextFlow infoTextFlow;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private ScrollPane scrollPane;

    private Stage window;

    void display() throws Exception
    {
        window = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("help.fxml"));
        window.setTitle("Scrabble: Help");
        window.setScene(new Scene(root, 1000, 1000));
        window.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setStyle("-fx-box-border: transparent");
        scrollPane.setFitToWidth(true);
        TreeItem<String> root, rules, controls, about;
        root = new TreeItem<>("Help Menu");
        root.setExpanded(false);
        rules = new TreeItem<>("Rules");
        controls = new TreeItem<>("Controls");
        about = new TreeItem<>("About");
        root.getChildren().addAll(rules, controls, about);
        treeView.setRoot(root);
        treeView.getSelectionModel().selectedItemProperty()
                .addListener((v, oldValue, newValue) -> {
                    if (newValue == null)
                    {
                        return;
                    }
                    if (newValue == root)
                    {
                        titleText.setText("Welcome to the Help Menu!");
                        infoTextFlow.setLineSpacing(1.25);
                        infoTextFlow.getChildren().clear();
                        Text intro = new Text("Here, you will find all you need to help get started playing Scrabble!");
                        infoTextFlow.getChildren().add(intro);
                    }
                    if (newValue == rules)
                    {
                        titleText.setText("Rules");
                        infoTextFlow.getChildren().clear();
                        Text intro = new Text(
                                "When playing Scrabble, anywhere from two to four players will enjoy the game. The object when playing is to score more points than other players. As words are placed on the game board, points are collected and each letter that is used in the game will have a different point value. The main strategy is to play words that have the highest possible score based on the combination of letters.\n\n"
                        );
                        Text boldedText1 = new Text("The Scrabble Board\n\n");
                        boldedText1.getStyleClass().add("help-subtitles");
                        Text text1 = new Text(
                          "A standard Scrabble board will consist of cells that are located in a large square grid. The board offers 15 cells high and 15 cells wide. The tiles used on the game will fit in each cell on the board.\n\n"
                        );
                        Text boldedText2 = new Text("The Scrabble Tiles\n\n");
                        boldedText2.getStyleClass().add("help-subtitles");
                        Text text2 = new Text(
                                "There are 100 tiles that are used in the game and 98 of them will contain letters and point values. There are 2 blank tiles that can be used as wild tiles to take the place of any letter. When a blank is played, it will remain in the game as the letter it substituted for.\n\n" +
                                "Different letters in the game will have various point values and this will depend on how rare the letter is and how difficult it may be to lay that letter. Blank tiles will have no point values.\n\n"
                        );
                        Text text3 = new Text(
                                "Below are the point values for each letter that is used in a Scrabble game.\n" +
                                        "\n" +
                                        "0 Points - Blank tile.\n" +
                                        "\n" +
                                        "1 Point - A, E, I, L, N, O, R, S, T and U.\n" +
                                        "\n" +
                                        "2 Points - D and G.\n" +
                                        "\n" +
                                        "3 Points - B, C, M and P.\n" +
                                        "\n" +
                                        "4 Points - F, H, V, W and Y.\n" +
                                        "\n" +
                                        "5 Points - K.\n" +
                                        "\n" +
                                        "8 Points - J and X.\n" +
                                        "\n" +
                                        "10 Points - Q and Z.\n" +
                                        "\n"
                        );
                        Text boldedText3 = new Text("Extra Point Values\n\n");
                        boldedText3.getStyleClass().add("help-subtitles");
                        Text text4 = new Text(
                                "When looking at the board, players will see that some squares offer multipliers. Should a tile be placed on these squares, the value of the tile will be multiplied by 2x or 3x. Some squares will also multiply the total value of the word and not just the single point value of one tile.\n" +
                                        "\n" +
                                        "Double Letter Scores - The light blue cells in the board are isolated and when these are used, they will double the value of the tile placed on that square.\n" +
                                        "\n" +
                                        "Triple Letter Score - The dark blue cell in the board will be worth triple the amount, so any tile placed here will earn more points.\n" +
                                        "\n" +
                                        "Double Word Score - When a cell is light red in colour, it is a double word cell and these run diagonally on the board, towards the four corners. When a word is placed on these squares, the entire value of the word will be doubled.\n" +
                                        "\n" +
                                        "Triple Word Score - The dark red square is where the high points can be earned as this will triple the word score. Placing any word on these squares will boos points drastically. These are found on all four sides of the board and are equidistant from the corners.\n" +
                                        "\n" +
                                        "One Single Use - When using the extra point squares on the board, they can only be used one time. If a player places a word here, it cannot be used as a multiplier by placing another word on the same square.\n" +
                                        "\nBingo - Playing a word that involves all seven letters of your rack yields a 50 point bonus.\n\n"
                        );

                        Text boldedText4 = new Text("Playing the Game\n\n");
                        boldedText4.getStyleClass().add("help-subtitles");
                        Text text5 = new Text(
                                "Every player will start the game by drawing seven tiles from the Scrabble bag. There are three options during any turn. The player can place a word, they can exchange tiles for new tiles or they can choose to pass. In most cases, players will try to place a word as the other two options will result in no score.\n" +
                                        "\n" +
                                        "When a player chooses to exchange tiles, they can choose to exchange one or all of the tiles they currently hold. After tiles are exchanged, the turn is over and players will have to wait until their next turn to place a word on the board.\n" +
                                        "\n" +
                                        "Players can choose to pass at any time. They will forfeit that turn and hope to be able to play the next time. If any player passes three times in a row, the game will end and the one with the highest score will win."
                                + "\n\n"
                                + "Once all tiles are gone from the bag and a single player has placed all of their tiles, the game will end and the player with the highest score wins."
                        );
                        Text credits = new Text("Adapted from http://www.scrabblepages.com/scrabble/rules/\n\n");
                        infoTextFlow.getChildren().addAll(intro, boldedText1, text1, boldedText2, text2, text3, boldedText3, text4, boldedText4, text5, credits);
                    }
                    if (newValue == controls)
                    {
                        titleText.setText("Controls");
                    }
                    if (newValue == about)
                    {
                        titleText.setText("About");
                    }
                });
    }
}
