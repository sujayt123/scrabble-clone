package util;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util.FunctionHelper.*;
import static util.TileHelper.scoreCharacter;

/**
 * A utility class for moves on a board.
 *
 * Created by sujay on 7/27/17.
 */
public class BoardHelper {

    /**
     * A representation of the special squares in this board.
     */
    private static List<List<String>> specialTiles =
            Arrays.asList(Arrays.asList("TW"," "," ","DL"," "," "," ","TW", " ", " ", " ", "DL", " ", " ", "TW"),
                    Arrays.asList(" ","DW"," "," "," ","TL"," "," ", " ", "TL", " ", " ", " ", "DW", " "),
                    Arrays.asList(" "," ","DW"," "," "," ","DL"," ", "DL", " ", " ", " ", "DW", " ", " "),
                    Arrays.asList("DL"," "," ","DW"," "," "," ","DL", " ", " ", " ", "DW", " ", " ", "DL"),
                    Arrays.asList(" "," "," "," "," "," "," "," "),
                    Arrays.asList(" "," "," "," "," "," "," "," "),
                    Arrays.asList(" "," "," "," "," "," "," "," "),
                    Arrays.asList(" "," "," "," "," "," "," "," "),
                    Arrays.asList(" "," "," "," "," "," "," "," "),
                    Arrays.asList(" "," "," "," "," "," "," "," "),
                    Arrays.asList("DL"," "," ","DW"," "," "," ","DL", " ", " ", " ", "DW", " ", " ", "DL"),
                    Arrays.asList(" "," ","DW"," "," "," ","DL"," ", "DL", " ", " ", " ", "DW", " ", " "),
                    Arrays.asList(" ","DW"," "," "," ","TL"," "," ", " ", "TL", " ", " ", " ", "DW", " "),
                    Arrays.asList("TW"," "," ","DL"," "," "," ","TW", " ", " ", " ", "DL", " ", " ", "TW"));


    /**
     * Returns if the board state in boardAfterAttemptedMove is a valid play off of boardBeforeMove
     * @param boardBeforeMove the scrabble board before the move
     * @param boardAfterAttemptedMove the scrabble board after the attempted move
     * @param trie the trie representing the dictionary to use for validation
     * @return true is boardAfterAttemptedMove is a valid state after boardBeforeMove, false otherwise
     */
    public static boolean validMove(List<List<Character>> boardBeforeMove,
                                    List<List<Character>> boardAfterAttemptedMove,
                                    Trie trie)
    {
        boolean isFirstTurn = forEachBoardSquareAsList((r, c) -> boardBeforeMove.get(r).get(c) == ' ').stream().allMatch(x -> x);

        // Get all pairs in which board differs from mainModel.
        List<Pair<Integer, Integer>> attempted_changed_coords = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return boardBeforeMove.get(r).get(c) != boardAfterAttemptedMove.get(r).get(c);
        }).collect(Collectors.toList());

        if (attempted_changed_coords.size() == 0)
        {
            return false;
        }

        // Determine if the play is vertical or horizontal.
        boolean playWasHorizontal = attempted_changed_coords.stream()
                .allMatch(x -> x.getKey().equals(attempted_changed_coords.get(0).getKey()));

        boolean playWasVertical = attempted_changed_coords.stream()
                .allMatch(x -> x.getValue().equals(attempted_changed_coords.get(0).getValue()));

        boolean valid = (playWasVertical || playWasHorizontal);

        List<List<Character>> transposeOfBoardBeforeMove = forEachBoardSquareAsNestedList((r, c) -> boardBeforeMove.get(c).get(r));


        HashSet<Character>[][] verticalCrossCheckSets = computeCrossCheckSets(boardBeforeMove, trie);
        HashSet<Character>[][] horizontalCrossCheckSetsForTransposeOfBoard = computeCrossCheckSets(transposeOfBoardBeforeMove, trie);
        HashSet<Character>[][] horizontalCrossCheckSets = new HashSet[15][15];
        forEachBoardSquareAsList((r, c) -> {
            horizontalCrossCheckSets[r][c] = horizontalCrossCheckSetsForTransposeOfBoard[c][r];
            return null;
        });


        if (playWasVertical){
            valid = valid && validVerticalPlay(boardBeforeMove, boardAfterAttemptedMove, trie, horizontalCrossCheckSets);
            int col = attempted_changed_coords.get(0).getValue();

            // Ensure that the word is indeed connected vertically (and is not just two disjoint words in the same col)
            int min_row_ind = attempted_changed_coords.stream().map(Pair::getKey).reduce((x, y) -> x < y ? x : y).get();
            int max_row_ind = attempted_changed_coords.stream().map(Pair::getKey).reduce((x, y) -> x > y ? x : y).get();

            valid = valid && IntStream.rangeClosed(min_row_ind, max_row_ind)
                    .allMatch(i -> boardAfterAttemptedMove.get(i).get(col) != ' ');
        }
        else
        {
            valid = valid && validHorizontalPlay(boardBeforeMove, boardAfterAttemptedMove, trie, verticalCrossCheckSets);
            int row = attempted_changed_coords.get(0).getKey();

            // Ensure that the word is indeed connected horizontally (and is not just two disjoint words in the same row)
            int min_col_ind = attempted_changed_coords.stream().map(Pair::getValue).reduce((x, y) -> x < y ? x : y).get();
            int max_col_ind = attempted_changed_coords.stream().map(Pair::getValue).reduce((x, y) -> x > y ? x : y).get();
            valid = valid && IntStream.rangeClosed(min_col_ind, max_col_ind)
                    .allMatch(j -> boardAfterAttemptedMove.get(row).get(j) != ' ');
        }

        if (isFirstTurn)
        {
            valid = valid
                    && attempted_changed_coords.indexOf(new Pair<>(7, 7)) != -1
                    && attempted_changed_coords.size() >= 2;
        }
        else
        {
            // All subsequent turns must consist of a play that is vertically or horizontally adjacent to at
            // least one other letter of a word that existed before this turn.
            valid = valid && attempted_changed_coords.stream().anyMatch(
                    (p) -> {
                        int r = p.getKey();
                        int c = p.getValue();
                        return (r > 0 && boardBeforeMove.get(r-1).get(c) != ' ')
                                || (r < 14 && boardBeforeMove.get(r+1).get(c) != ' ')
                                || (c > 0 && boardBeforeMove.get(r).get(c-1) != ' ')
                                || (c < 14 && boardBeforeMove.get(r).get(c+1) != ' ');
                    });

        }
        return valid;
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

        int score = 0;

        // Get all pairs in which board differs from mainModel.
        List<Pair<Integer, Integer>> changed_coords = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return boardBeforeMove.get(r).get(c) != boardAfterValidatedMove.get(r).get(c);
        }).collect(Collectors.toList());

        boolean playWasHorizontal = changed_coords.stream()
                .allMatch(x -> x.getKey().equals(changed_coords.get(0).getKey()));

        if (!playWasHorizontal)
        {
            score += scoreVertical(boardBeforeMove, boardAfterValidatedMove, changed_coords.get(0));
            score += changed_coords.stream().map(x-> scoreHorizontal(boardBeforeMove, boardAfterValidatedMove, x)).reduce((x,y)->x+y).get();
        }
        else
        {
            score += scoreHorizontal(boardBeforeMove, boardAfterValidatedMove, changed_coords.get(0));
            score += changed_coords.stream().map(x -> scoreVertical(boardBeforeMove, boardAfterValidatedMove, x)).reduce((x,y)->x+y).get();
        }
        if (changed_coords.size() == 7)
        {
            score += 50;
        }

        return score;
    }

    /**
     * Requires:
     * validMove(boardBeforeMove, boardAfterMove) to be true
     *
     * Returns the score for the move represented by the difference boardAfterMove and boardBeforeMove.
     * @param boardBeforeValidatedMove the scrabble board before the validated move
     * @param boardAfterValidatedMove the scrabble board after the validated move
     * @param coord the coordinate for which we should score a vertically oriented word for the play
     * @return the score for the vertical word involving coord in boardAfterValidatedMove
     */
    private static int scoreVertical(List<List<Character>> boardBeforeValidatedMove,
                                     List<List<Character>> boardAfterValidatedMove,
                                     Pair<Integer, Integer> coord)
    {
        int col = coord.getValue();

        Pair<String, Integer> p = buildVerticalWordForCoordinate(boardAfterValidatedMove, coord);
        int start_index = p.getValue();
        int length = p.getKey().length();

        // If there is no word (of 2 or more letters) formed, return 0 immediately.
        if (length == 1)
        {
            return 0;
        }

        Triple<Integer, Integer, Integer> wordScoreTuple =
                IntStream.range(start_index, start_index + length)
                        .mapToObj(r -> r)
                        .reduce(new Triple<>(0, 0, 0),
                                (acc, r) -> {
                                    int partialScore = acc.getA();
                                    int dw_count = acc.getB();
                                    int tw_count = acc.getC();
                                    int letterScore = scoreCharacter(boardAfterValidatedMove.get(r).get(col));
                                    if (!specialTiles.get(r).get(col).equals(" ") && boardBeforeValidatedMove.get(r).get(col) == ' ') {
                                        switch (specialTiles.get(r).get(col)) {
                                            case "DW":
                                                dw_count++;
                                                break;
                                            case "TW":
                                                tw_count++;
                                                break;
                                            case "DL":
                                                letterScore *= 2;
                                                break;
                                            case "TL":
                                                letterScore *= 3;
                                                break;
                                        }
                                    }
                                    return new Triple<>(partialScore + letterScore, dw_count, tw_count);
                                }
                                , (tripleA, tripleB) ->
                                        new Triple<>(tripleA.getA() + tripleB.getA(),
                                                tripleA.getB() + tripleB.getB(),
                                                tripleA.getC() + tripleB.getC())
                        );

        int baseScore = wordScoreTuple.getA();
        int dw_count = wordScoreTuple.getB();
        int tw_count = wordScoreTuple.getC();

        return (int) (baseScore * Math.pow(2, dw_count) * Math.pow(3, tw_count));
    }

    /**
     * Requires:
     * validMove(boardBeforeMove, boardAfterMove) to be true
     *
     * Returns the score for the move represented by the difference boardAfterMove and boardBeforeMove.
     *
     * @param boardBeforeValidatedMove the scrabble board before the validated move
     * @param boardAfterValidatedMove the scrabble board after the validated move
     * @param coord the coordinate for which we should score a vertically oriented word for the play
     * @return the score for the horizontal word involving coord in boardAfterValidatedMove
     */
    private static int scoreHorizontal(List<List<Character>> boardBeforeValidatedMove, List<List<Character>> boardAfterValidatedMove, Pair<Integer, Integer> coord)
    {
        int row = coord.getKey();

        Pair<String, Integer> p = buildHorizontalWordForCoordinate(boardAfterValidatedMove, coord);
        int start_index = p.getValue();
        int length = p.getKey().length();

        // If there is no word (of 2 or more letters) formed, return 0 immediately.
        if (length == 1)
        {
            return 0;
        }

        Triple<Integer, Integer, Integer> wordScoreTuple =
                IntStream.range(start_index, start_index + length)
                        .mapToObj(c -> c)
                        .reduce(new Triple<>(0, 0, 0),
                                (acc, c) -> {
                                    int partialScore = acc.getA();
                                    int dw_count = acc.getB();
                                    int tw_count = acc.getC();
                                    int letterScore = scoreCharacter(boardAfterValidatedMove.get(row).get(c));
                                    if (!specialTiles.get(row).get(c).equals(" ") && boardBeforeValidatedMove.get(row).get(c) == ' ') {
                                        switch (specialTiles.get(row).get(c)) {
                                            case "DW":
                                                dw_count++;
                                                break;
                                            case "TW":
                                                tw_count++;
                                                break;
                                            case "DL":
                                                letterScore *= 2;
                                                break;
                                            case "TL":
                                                letterScore *= 3;
                                                break;
                                        }
                                    }
                                    return new Triple<>(partialScore + letterScore, dw_count, tw_count);
                                }
                                , (tripleA, tripleB) ->
                                        new Triple<>(tripleA.getA() + tripleB.getA(),
                                                tripleA.getB() + tripleB.getB(),
                                                tripleA.getC() + tripleB.getC())
                        );

        int baseScore = wordScoreTuple.getA();
        int dw_count = wordScoreTuple.getB();
        int tw_count = wordScoreTuple.getC();

        return (int) (baseScore * Math.pow(2, dw_count) * Math.pow(3, tw_count));
    }

    /**
     * Builds the vertical word in which the letter at the provided coordinate in the provided model
     * If the provided coordinate is empty, returns the prefix to the word that would exist if a tile were placed there.
     *
     * @param board the model to use for construction of the word
     * @param pair coordinate
     * @return the word itself, as well as the starting index of the word
     */
    public static Pair<String, Integer> buildVerticalWordForCoordinate(List<List<Character>> board, Pair<Integer, Integer> pair)
    {
        StringBuilder sb = new StringBuilder();
        int row = pair.getKey();
        int col = pair.getValue();

        OptionalInt top_exclusive = IntStream.iterate(row - 1, i -> i - 1)
                .limit(row)
                .filter(r -> board.get(r).get(col) == ' ')
                .findFirst();
        OptionalInt bot_exclusive = IntStream.range(row, 15)
                .filter(r -> board.get(r).get(col) == ' ')
                .findFirst();
        int top_exc = top_exclusive.isPresent() ? top_exclusive.getAsInt(): -1;
        int bot_exc = bot_exclusive.isPresent() ? bot_exclusive.getAsInt(): 15;
        IntStream.range(top_exc + 1, bot_exc)
                .forEach(r ->
                        sb.append(board.get(r).get(col))
                );

        return new Pair<>(sb.length() > 0 ? sb.toString() : "", top_exc + 1);
    }

    /**
     * Builds the vertical word in which the letter at the provided coordinate in the provided model
     * If the provided coordinate is empty, returns the prefix to the word that would exist if a tile were placed there.
     *
     * @param board the model to use for construction of the word
     * @param pair coordinate
     * @return the word itself, as well as the starting index of the word
     */
    public static Pair<String, Integer> buildHorizontalWordForCoordinate(List<List<Character>> board, Pair<Integer, Integer> pair)
    {
        StringBuilder sb = new StringBuilder();
        int row = pair.getKey();
        int col = pair.getValue();

        OptionalInt top_exclusive = IntStream.iterate(col - 1, c -> c - 1)
                .limit(col)
                .filter(c -> board.get(row).get(c) == ' ')
                .findFirst();
        OptionalInt bot_exclusive = IntStream.range(col, 15)
                .filter(c -> board.get(row).get(c) == ' ')
                .findFirst();
        int left_exc = top_exclusive.isPresent() ? top_exclusive.getAsInt(): -1;
        int right_exc = bot_exclusive.isPresent() ? bot_exclusive.getAsInt(): 15;
        IntStream.range(left_exc + 1, right_exc)
                .forEach(c ->
                        sb.append(board.get(row).get(c))
                );

        return new Pair<>(sb.length() > 0 ? sb.toString() : "", left_exc + 1);
    }

    /**
     * Is the vertical play a valid word in the vertical direction?
     * @param boardBeforeAttemptedMove the scrabble board before the attempted move
     * @param boardAfterAttemptedMove the scrabble board after the attempted move
     * @param trie the trie representing the dictionary to use for validation
     * @param horizontalCrossCheckSets the horizontal cross check sets for boardBeforeAttemptedMove
     * @return true if the play is a valid vertical play, false otherwise
     */
    private static boolean validVerticalPlay(List<List<Character>> boardBeforeAttemptedMove,
                                      List<List<Character>> boardAfterAttemptedMove,
                                      Trie trie,
                                      HashSet<Character>[][] horizontalCrossCheckSets) {

        boolean isFirstTurn = forEachBoardSquareAsList((r, c) -> boardBeforeAttemptedMove.get(r).get(c) == ' ').stream().allMatch(x -> x);

        // Get all pairs in which board differs from mainModel.
        List<Pair<Integer, Integer>> attempted_changed_coords = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return boardBeforeAttemptedMove.get(r).get(c) != boardAfterAttemptedMove.get(r).get(c);
        }).collect(Collectors.toList());

        // First, check if the vertical part constitutes a word.
        String verticalWord = buildVerticalWordForCoordinate(boardAfterAttemptedMove, attempted_changed_coords.get(0)).getKey();
        // Now that we have the vertical word we've formed, let's see whether it is valid.

        TrieNode tn = trie.getNodeForPrefix(verticalWord);

        // Second, check if the horizontal words formed in a parallel play follow the cross sets.
        return (verticalWord.length() == 1 || (tn != null && tn.isWord()))
                && (attempted_changed_coords.stream().allMatch((pair) ->
                horizontalCrossCheckSets[pair.getKey()][pair.getValue()]
                        .contains(boardAfterAttemptedMove.get(pair.getKey()).get(pair.getValue())))
                || isFirstTurn);
    }

    /**
     * Is this horizontal play valid in the horizontal direction?
     * @param boardBeforeAttemptedMove the scrabble board before the attempted move
     * @param boardAfterAttemptedMove the scrabble board after the attempted move
     * @param trie the trie representing the dictionary to use for validation
     * @param verticalCrossCheckSets the cross check sets for boardBeforeAttemptedMove
     * @return true if the play is a valid horizontal play, false otherwise
     */
    private static boolean validHorizontalPlay(List<List<Character>> boardBeforeAttemptedMove,
                                               List<List<Character>> boardAfterAttemptedMove,
                                               Trie trie,
                                               HashSet<Character>[][] verticalCrossCheckSets)
    {
        boolean isFirstTurn = forEachBoardSquareAsList((r, c) -> boardBeforeAttemptedMove.get(r).get(c) == ' ').stream().allMatch(x -> x);

        // Get all pairs in which board differs from mainModel.
        List<Pair<Integer, Integer>> attempted_changed_coords = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return boardBeforeAttemptedMove.get(r).get(c) != boardAfterAttemptedMove.get(r).get(c);
        }).collect(Collectors.toList());

        // First, check if the vertical part constitutes a word.
        String horizontalWord = buildHorizontalWordForCoordinate(boardAfterAttemptedMove, attempted_changed_coords.get(0)).getKey();

        // Now that we have the vertical word we've formed, let's see whether it is valid.
        TrieNode tn = trie.getNodeForPrefix(horizontalWord);

        // Second, check if the vertical words formed in a parallel play follow the cross sets.
        return (horizontalWord.length() == 1 || (tn != null && tn.isWord()))
                && (attempted_changed_coords.stream().
                allMatch((pair) -> verticalCrossCheckSets[pair.getKey()][pair.getValue()]
                        .contains(boardAfterAttemptedMove.get(pair.getKey()).get(pair.getValue())))
                || isFirstTurn);
    }

    /**
     * Computes the VERTICAL cross check sets for a given model.
     *
     * To compute the horizontal cross check sets for that model,
     * pass in model transpose and take the transpose of the output
     * of this function.
     *
     * @param model the scrabble board
     * @param trie the trie to use for validation of plays in the board
     * @return the vertical cross-check sets for model under the constraints of trie
     */
    public static HashSet<Character>[][] computeCrossCheckSets(List<List<Character>> model,
                                       Trie trie){
        HashSet<Character>[][] crossCheckSets = new HashSet[15][15];

        getCoordinatesListForBoard().stream().filter(pair -> model.get(pair.getKey()).get(pair.getValue()) == ' ').forEach((pair) -> {
            int i = pair.getKey();
            int j = pair.getValue();
            Pair<String, Integer> verticalPrefixToThisSquare = buildVerticalWordForCoordinate(model, pair);
            TrieNode prefixNode = trie.getNodeForPrefix(verticalPrefixToThisSquare.getKey());
            if (prefixNode != null)
            {
                StringBuilder verticalSuffixToThisSquare = new StringBuilder();
                OptionalInt bot_exclusive = IntStream.range(i + 1, 15)
                        .filter(r-> model.get(r).get(j) == ' ')
                        .findFirst();
                IntStream.range(i + 1, bot_exclusive.isPresent() ? bot_exclusive.getAsInt() : 15)
                        .forEach(x -> {
                            verticalSuffixToThisSquare.append(model.get(x).get(j));
                        });

                prefixNode.getOutgoingEdges().keySet().forEach(c -> {
                    if (prefixNode.getNodeForPrefix(c + verticalSuffixToThisSquare.toString(), 0) != null
                            && prefixNode.getNodeForPrefix(c + verticalSuffixToThisSquare.toString(), 0).isWord()) {
                        crossCheckSets[i][j].add(c);
                    }
                });

                if (prefixNode == trie.getRoot() && verticalSuffixToThisSquare.toString().equals(""))
                {
                    crossCheckSets[i][j].addAll(forEachAtoZ(c->c));
                }
            }
        });
        return crossCheckSets;
    }
}
