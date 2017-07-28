package ai;

import javafx.util.Pair;
import util.Quadruple;
import util.Trie;
import util.TrieNode;
import util.Triple;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util.FunctionHelper.forEachBoardSquareAsList;
import static util.FunctionHelper.forEachBoardSquareAsNestedList;
import static util.FunctionHelper.getCoordinatesListForBoard;
import static util.BoardHelper.*;

/**
 * A class that implements an [efficient] Scrabble solver algorithm.
 *
 * Created by sujay on 7/27/17.
 */
public class AI {

    private static List<Pair<Integer, Integer>> generateVerticalNeighbors(int r, int c)
    {
        List<Pair<Integer, Integer>> s = new ArrayList<>();
        if (r > 0)
        {
            s.add(new Pair<>(r - 1, c));
        }
        if (r < 14)
        {
            s.add(new Pair<>(r + 1, c));
        }
        return s;
    }

    private static List<Pair<Integer, Integer>> generateHorizontalNeighbors(int r, int c)
    {
        List<Pair<Integer, Integer>> s = new ArrayList<>();
        if (c > 0)
        {
            s.add(new Pair<>(r, c - 1));
        }
        if (c < 14)
        {
            s.add(new Pair<>(r, c + 1));
        }
        return s;
    }

    /**
     * Returns the state of the Scrabble board after the CPU moves.
     *
     * The client should assume that in all cases when no move is possible with the CPU hand,
     * the CPU defaults to swapping tiles if the bagSize >=7, else pass the turn.
     *
     * @param input the scrabble board before the AI takes its turn,
     *              the cpu hand before the turn,
     *              the tile bag before the turn,
     *              the trie containing the dictionary of valid words
     * @return the scrabble board after the AI takes its turn,
     *              the cpu hand after the turn,
     *              the tile bag after the turn
     */
    public static Triple<List<List<Character>>, List<Character>, Queue<Character>> CPUMove(
            Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input)
    {

        List<List<Character>> boardBeforeCPUMove = input.getA();
        List<Character> cpuHand = input.getB();
        Queue<Character> tilesRemaining = input.getC();
        Trie trie = input.getD();

        boolean isFirstTurn = forEachBoardSquareAsList((r, c) -> boardBeforeCPUMove.get(r).get(c) == ' ').stream().allMatch(x -> x);

        Set<Pair<Integer, Integer>> anchorSquares;

        if(!isFirstTurn){
            anchorSquares = forEachBoardSquareAsList((r, c) -> {
                boolean validAnchorSquare = (boardBeforeCPUMove.get(r).get(c) == ' ');
                List<Pair<Integer, Integer>> neighbors = generateVerticalNeighbors(r, c);
                neighbors.addAll(generateHorizontalNeighbors(r, c));
                validAnchorSquare = validAnchorSquare && neighbors.stream().anyMatch((pair) ->
                        boardBeforeCPUMove.get(pair.getKey()).get(pair.getValue()) != ' ');
                if (validAnchorSquare)
                    return new Pair<>(r,c);
                return null;
            }).stream().filter(Objects::nonNull).collect(Collectors.toSet());
        }
        else
        {
            anchorSquares = getCoordinatesListForBoard().stream().collect(Collectors.toSet());
        }

        List<List<Character>> copyOfMainModel = forEachBoardSquareAsNestedList((r, c) -> boardBeforeCPUMove.get(r).get(c));
        List<List<Character>> transposeOfMainModel = forEachBoardSquareAsNestedList((r, c) -> boardBeforeCPUMove.get(c).get(r));

        HashSet<Character>[][] verticalCrossCheckSets = computeCrossCheckSets(copyOfMainModel, trie);
        HashSet<Character>[][] horizontalCrossCheckSetsForTransposeOfBoard = computeCrossCheckSets(transposeOfMainModel, trie);


        Set<Pair<Integer, Integer>> transposedAnchorSquares =
                anchorSquares.stream().map(x -> new Pair<>(x.getValue(), x.getKey())).collect(Collectors.toSet());

        //TODO
//        statusMessage.getStyleClass().clear();
//        statusMessage.getStyleClass().add("success-text");

        Triple<List<List<Character>>, String, Integer> bestCPUPlay = new Triple<>(null, "", Integer.MIN_VALUE);

        anchorSquares.forEach(square -> computeBestHorizontalPlayAtAnchor(copyOfMainModel, cpuHand, anchorSquares, square, verticalCrossCheckSets, trie, false));
        transposedAnchorSquares.forEach(square -> computeBestHorizontalPlayAtAnchor(transposeOfMainModel, transposedAnchorSquares, square, horizontalCrossCheckSetsForTransposeOfBoard, true));
        ArrayList<Pair<Integer, Integer>> newSquaresPlacedLocations = new ArrayList<>();

        List<List<Character>> bestScoringBoard = bestCPUPlay.getKey();

        // Reset the default colors of text on the board
        forEachBoardSquareAsList((r, c) -> {
            if (mainModel.get(r).get(c) != ' ')
            {
                viewModel.get(r).get(c).getStyleClass().removeAll("bold-text");
                viewModel.get(r).get(c).getStyleClass().add("black-text");
            }
            return null;
        });

        if (bestScoringBoard != null)
        {
            cpuConsecutiveZeroScoringTurns = 0;
            mainModel = forEachBoardSquareAsNestedList((r, c) -> {
                // Side effects on the View Model
                if (bestScoringBoard.get(r).get(c) != mainModel.get(r).get(c))
                {
                    viewModel.get(r).get(c).setText(bestScoringBoard.get(r).get(c) + "");
                    viewModel.get(r).get(c).getStyleClass().add("bold-text");
                    board_cells[r][c].getStyleClass().add("played-tile");
                }
                cpuHand.remove((Character)bestScoringBoard.get(r).get(c));
                return bestScoringBoard.get(r).get(c);
            });
        }
        else
        {
            cpuConsecutiveZeroScoringTurns++;
//            System.out.println("Could not find anything to play with these characters");
            if (tilesRemaining.size() >= 7)
            {
                // Attempt swap by dumping all cpu tiles into bag, and then randomly redrawing 7.
                tilesRemaining.addAll(cpuHand);
                List<Character> shuffledTiles = new ArrayList<>(tilesRemaining);
                Collections.shuffle(shuffledTiles);
                tilesRemaining.clear();
                tilesRemaining.addAll(shuffledTiles);
                cpuHand.clear();
                IntStream.range(0, 7).forEach(i -> cpuHand.add(tilesRemaining.poll()));
                statusMessage.setText("CPU swapped some tiles.");
            }
            else
            {
                statusMessage.setText("CPU passed the turn.");
            }
            if (gameOver(cpuHand, cpuConsecutiveZeroScoringTurns))
            {
                cleanup();
            }
            return;
        }

        List<List<Character>> transposeOfUpdatedModel = forEachBoardSquareAsNestedList((r, c) -> mainModel.get(c).get(r));
        computeCrossCheckSets(verticalCrossCheckSetsForModel, mainModel, getCoordinatesListForBoard());
        computeCrossCheckSets(horizontalCrossCheckSetsForModelTranspose, transposeOfUpdatedModel, getCoordinatesListForBoard());
        int score = Integer.parseInt(cpuScore.getText().split(":")[1]);
        score += bestCPUPlay.getValue().getValue();
        cpuScore.setText("CPU Score:" + score);
        statusMessage.setText("CPU played " + bestCPUPlay.getValue().getKey() + " for " + bestCPUPlay.getValue().getValue() + " points.");
        for (int k = cpuHand.size(); k < 7; k++)
        {
            Character c = tilesRemaining.poll();
            if (c != null)
            {
                cpuHand.add(c);
            }
        }
        isFirstTurn = false;
        if (gameOver(cpuHand, cpuConsecutiveZeroScoringTurns))
        {
            cleanup();
        }
    }

    /**
     * Computes the best horizontal play at the provided anchor and
     * @param boardBeforeCPUMove the state of the board before the cpu makes its move.
     * @param cpuHand the list of characters in the cpu's hand
     * @param anchors the set of all anchor squares for this board
     * @param square the particular square we're looking at right now
     * @param verticalCrossCheckSets the vertical cross check sets for this board
     * @param trie the trie representing every word in the accepted dictionary
     * @param transposed true if the board provided is a transpose of the actual model, false otherwise
     */
    private static void computeBestHorizontalPlayAtAnchor(List<List<Character>> boardBeforeCPUMove,
                                                   List<Character> cpuHand,
                                                   Set<Pair<Integer, Integer>> anchors,
                                                   Pair<Integer, Integer> square,
                                                   HashSet<Character>[][] verticalCrossCheckSets,
                                                   Trie trie,
                                                   boolean transposed) {
        int col = square.getValue();
        List<List<Character>> mutableBoard =
                forEachBoardSquareAsNestedList((r, c) -> boardBeforeCPUMove.get(r).get(c));

        OptionalInt left_exclusive = IntStream.iterate(col - 1, i -> i - 1)
                .limit(col)
                .filter(c -> boardBeforeCPUMove.get(square.getKey()).get(c) != ' '
                        || anchors.contains(new Pair<>(square.getKey(), c)))
                .findFirst();
        int k = left_exclusive.isPresent() ? col - left_exclusive.getAsInt() - 1: col;
        if (k == 0)
        {
            if (col == 0)
            {
                ExtendRight(boardBeforeCPUMove, mutableBoard, square, "", cpuHand,
                        trie.getRoot(), verticalCrossCheckSets, transposed);
            }
            else if (boardBeforeCPUMove.get(square.getKey()).get(col - 1) != ' ')
            {
                String prefix = buildHorizontalWordForCoordinate(boardBeforeCPUMove,
                        new Pair<>(square.getKey(), col - 1)).getKey();

                ExtendRight(boardBeforeCPUMove, mutableBoard, square, prefix, cpuHand,
                        trie.getNodeForPrefix(prefix), verticalCrossCheckSets, transposed);
            }
        }
        LeftPart(boardBeforeCPUMove, mutableBoard, square,"", cpuHand,
                trie.getRoot(), verticalCrossCheckSets, k, k, transposed);
    }

    /**
     *
     * @param boardBeforeMove
     * @param board
     * @param square
     * @param partialWord
     * @param tilesRemainingInRack
     * @param N
     * @param crossCheckSets
     * @param limit
     * @param maxLimit
     * @param transposed
     */
    private static void LeftPart(List<List<Character>> boardBeforeMove, List<List<Character>> board, Pair<Integer,Integer> square, String partialWord, List<Character> tilesRemainingInRack, TrieNode N, HashSet<Character>[][] crossCheckSets, int limit, int maxLimit, boolean transposed)
    {
        ExtendRight(boardBeforeMove, board, square, partialWord, tilesRemainingInRack, N, crossCheckSets, transposed);
        if (limit > 0)
        {
            N.getOutgoingEdges().keySet().forEach(c -> {
                if (tilesRemainingInRack.contains((Character)c))
                {
                    for (int i = square.getValue() - maxLimit; i < square.getValue(); i++)
                    {
                        board.get(square.getKey()).set(i, board.get(square.getKey()).get(i + 1));
                    }
                    board.get(square.getKey()).set(square.getValue() - 1, c);
                    tilesRemainingInRack.remove((Character)c);
                    LeftPart(boardBeforeMove, board, square, partialWord + c, tilesRemainingInRack, N.getOutgoingEdges().get(c), crossCheckSets, limit - 1, maxLimit, transposed);
                    tilesRemainingInRack.add(c);
                    for (int i = square.getValue() - 1; i > square.getValue() - maxLimit; i--)
                    {
                        board.get(square.getKey()).set(i, board.get(square.getKey()).get(i - 1));
                    }
                    board.get(square.getKey()).set(square.getValue() - maxLimit,' ');
                }
            });
        }
    }


    /**
     *
     * @param boardBeforeCPUMove
     * @param board
     * @param square
     * @param partialWord
     * @param tilesRemainingInRack
     * @param N
     * @param crossCheckSets
     * @param transposed
     */
    private static void ExtendRight(List<List<Character>> boardBeforeCPUMove, List<List<Character>> board, Pair<Integer, Integer> square, String partialWord, List<Character> tilesRemainingInRack, TrieNode N, HashSet<Character>[][] crossCheckSets, boolean transposed)
    {
//        for (int i = 0 ; i < 15 ; i++) {
//            for (int j = 0; j< 15; j++)
//            {
//                System.out.print(board[i][j]);
//            }
//            System.out.println();
//        }
        if (square.getValue() >= 15)
            return;
        if (board.get(square.getKey()).get(square.getValue()) == ' ')
        {
            if (N.isWord())
            {
                LegalMove(board, partialWord, transposed);
            }
            N.getOutgoingEdges().keySet().forEach(c -> {
                if (tilesRemainingInRack.contains(c) && crossCheckSets[square.getKey()][square.getValue()].contains(c))
                {
                    tilesRemainingInRack.remove((Character)c);
                    board.get(square.getKey()).set(square.getValue(), c);
                    ExtendRight(board, new Pair<>(square.getKey(), square.getValue() + 1), partialWord + c, tilesRemainingInRack, N.getOutgoingEdges().get(c), crossCheckSets, transposed);
                    board.get(square.getKey()).set(square.getValue(), ' ');
                    tilesRemainingInRack.add(c);
                }
            });
        }
        else
        {
            if (N.getOutgoingEdges().containsKey(board.get(square.getKey()).get(square.getValue())))
            {
                ExtendRight(board, new Pair<>(square.getKey(), square.getValue() + 1), partialWord + board.get(square.getKey()).get(square.getValue()), tilesRemainingInRack, N.getOutgoingEdges().get(board.get(square.getKey()).get(square.getValue())), crossCheckSets, transposed);
            }
        }
    }

    private void LegalMove(List<List<Character>> b, String partialWord,
                           boolean transposed,
                           Triple<List<List<Character>>, String, Integer>) {

        List<List<Character>> board = transposed ? forEachBoardSquareAsNestedList((r, c) -> b.get(c).get(r)) : b;

        // Get all pairs in which board differs from mainModel.
        List<Pair<Integer, Integer>> changed_coords_by_cpu = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return board.get(r).get(c) != mainModel.get(r).get(c);
        }).collect(Collectors.toList());

        if (!isValidMove(board, changed_coords_by_cpu))
            return;

        int score;
        if (transposed)
        {
            score = scoreVertical(board, changed_coords_by_cpu.get(0));
            score += changed_coords_by_cpu.stream().map(p -> scoreHorizontal(board, p)).reduce((x, y) -> x+y).get();
        }
        else
        {
            score = scoreHorizontal(board, changed_coords_by_cpu.get(0));
            score += changed_coords_by_cpu.stream().map(p -> scoreVertical(board, p)).reduce((x, y) -> x+y).get();
        }

        if (changed_coords_by_cpu.size() == 7)
        {
            score += 50;
        }

        if (score > bestCPUPlay.getValue().getValue())
        {
//            System.out.println("The word " + partialWord + " garnered " + score + " points for the CPU");
            bestCPUPlay = new Pair<>(forEachBoardSquareAsNestedList((r, c) -> board.get(r).get(c)),
                    new Pair<>(partialWord.concat(""), score));
        }
    }

}


