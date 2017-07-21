package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    /* Access to the GUI representation of the board. */
    private StackPane[][] board_cells = new StackPane[15][15];

    /* Access to the GUI GridPane.*/
    @FXML
    GridPane gridPane;

    /*
     * Runs initialization routines right after the view loads.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.getChildren().forEach((child)->{
            if (child instanceof StackPane)
            {
                final int row = gridPane.getRowIndex(child);
                final int col = gridPane.getColumnIndex(child);
                board_cells[row][col] = (StackPane) child;
            }
        });
    }
}
