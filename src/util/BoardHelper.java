package util;

import javafx.util.Pair;

import java.util.List;

/**
 * A utility class for moves on a board.
 *
 * Created by sujay on 7/27/17.
 */
public class BoardHelper {

    /**
     * Returns if the board state in boardAfterAttemptedMove is a valid play off of boardBeforeMove
     * @param boardBeforeMove the scrabble board before the move
     * @param boardAfterAttemptedMove the scrabble board after the attempted move
     * @return
     */
    public static boolean validMove(List<List<Character>> boardBeforeMove, List<List<Character>> boardAfterAttemptedMove)
    {
        return false;
    }

    /**
     * Requires:
     * validMove(boardBeforeMove, boardAfterMove) to be true
     *
     * Returns the score for the move represented by the difference boardAfterMove and boardBeforeMove.
     * @param boardBeforeMove the scrabble board before the move
     * @param boardAfterValidatedMove the scrabble board after the validated move
     * @return the score for the play
     */
    public static int scoreMove(List<List<Character>> boardBeforeMove, List<List<Character>> boardAfterValidatedMove)
    {
        return -1;
    }

    /**
     * Requires:
     * validMove(boardBeforeMove, boardAfterMove) to be true
     *
     * Returns the score for the move represented by the difference boardAfterMove and boardBeforeMove.
     * @param boardAfterValidatedMove the scrabble board after the validated move
     * @param coord the coordinate for which we should score a vertically oriented word for the play
     * @return the score for the vertical word involving coord in boardAfterValidatedMove
     */
    private static int scoreVertical(List<List<Character>> boardAfterValidatedMove, Pair<Integer, Integer> coord)
    {
        return -1;
    }

    /**
     * Requires:
     * validMove(boardBeforeMove, boardAfterMove) to be true
     *
     * Returns the score for the move represented by the difference boardAfterMove and boardBeforeMove.
     * @param boardAfterValidatedMove the scrabble board after the validated move
     * @param coord the coordinate for which we should score a vertically oriented word for the play
     * @return the score for the vertical word involving coord in boardAfterValidatedMove
     */
    private static int scoreHorizontal(List<List<Character>> boardAfterValidatedMove, Pair<Integer, Integer> coord)
    {
        return -1;
    }
}
