package API;

import API.Trie;
import javafx.util.Pair;
import util.Quadruple;
import util.TrieNode;
import util.Triple;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static API.Board.*;
import static util.FunctionHelper.*;

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
     * Returns the state of the Scrabble board after the CPU plays the highest possible scoring word from his position.
     *
     * The client should assume that in all cases when no move is possible with the CPU hand,
     * the CPU defaults to swapping tiles if the bagSize is greater than or equal to 7, else pass the turn.
     *
     * @param input the scrabble board before the AI takes its turn,
     *              the cpu hand before the turn,
     *              the tile bag before the turn,
     *              the trie containing the dictionary of valid words
     * @return the scrabble board after the AI takes its turn,
     *              the cpu hand after the turn,
     *              the tile bag after the turn,
     *              a pair of the string played and the score yielded by that string
     */
    public static Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> CPUMove(
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
            anchorSquares = new HashSet<>(Arrays.asList(new Pair<>(7,7)));
        }

        List<List<Character>> copyOfMainModel = forEachBoardSquareAsNestedList((r, c) -> boardBeforeCPUMove.get(r).get(c));
        List<List<Character>> transposeOfMainModel = forEachBoardSquareAsNestedList((r, c) -> boardBeforeCPUMove.get(c).get(r));
        List<Character> copyOfCPUHand = cpuHand.stream().map(x->x).collect(Collectors.toList());

        HashSet<Character>[][] verticalCrossCheckSets = computeCrossCheckSets(copyOfMainModel, trie);
        HashSet<Character>[][] horizontalCrossCheckSetsForTransposeOfBoard = computeCrossCheckSets(transposeOfMainModel, trie);


        Set<Pair<Integer, Integer>> transposedAnchorSquares =
                anchorSquares.stream().map(x -> new Pair<>(x.getValue(), x.getKey())).collect(Collectors.toSet());

        Triple<List<List<Character>>, String, Integer> bestCPUPlay = new Triple<>(null, "", Integer.MIN_VALUE);

        anchorSquares.forEach(square -> computeBestHorizontalPlayAtAnchor(copyOfMainModel, copyOfCPUHand, anchorSquares, square, verticalCrossCheckSets, trie, false, bestCPUPlay));
        transposedAnchorSquares.forEach(square -> computeBestHorizontalPlayAtAnchor(transposeOfMainModel, copyOfCPUHand, transposedAnchorSquares, square, horizontalCrossCheckSetsForTransposeOfBoard, trie, true, bestCPUPlay));

        List<List<Character>> bestScoringBoard = bestCPUPlay.getA();
        List<Character> newCPUHand = cpuHand.stream().map(x->x).collect(Collectors.toList());
        Queue<Character> newTilesRemaining = new ArrayDeque<>(tilesRemaining.stream().map(x->x).collect(Collectors.toList()));

        if (bestScoringBoard == null)
        {
            if (tilesRemaining.size() >= 7)
            {
                // Attempt swap by randomly drawing 7 tiles, and adding the 7 former tiles back into the bag.
                newCPUHand.clear();
                for (int i = 0; i < 7; i++)
                {
                    newCPUHand.add(newTilesRemaining.poll());
                    newTilesRemaining.add(cpuHand.get(i));
                }
                // Shuffle the bag of tiles.
                ArrayList<Character> temp = new ArrayList<>(newTilesRemaining);
                Collections.shuffle(temp);
                newTilesRemaining = new ArrayDeque<>(temp);
            }
            return new Quadruple<>(copyOfMainModel, newCPUHand, newTilesRemaining, new Pair<>("", 0));
        }

        // Get all pairs in which the board after the move differs from the board before the move.
        List<Pair<Integer, Integer>> attempted_changed_coords = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return bestScoringBoard.get(r).get(c) != boardBeforeCPUMove.get(r).get(c);
        }).collect(Collectors.toList());

        attempted_changed_coords.forEach(x -> newCPUHand.remove((Character)bestScoringBoard.get(x.getKey()).get(x.getValue())));

        int score = bestCPUPlay.getC();

        for (int k = newCPUHand.size(); k < 7; k++)
        {
            Character c = newTilesRemaining.poll();
            if (c != null)
            {
                newCPUHand.add(c);
            }
        }

        return new Quadruple<>(bestScoringBoard, newCPUHand, newTilesRemaining, new Pair<>(bestCPUPlay.getB(), score));
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
     * @param bestCPUPlay a mutable instance of a triple containing
     *                    (best possible board state after move,
     *                    string, score for play)
     */
    private static void computeBestHorizontalPlayAtAnchor(List<List<Character>> boardBeforeCPUMove,
                                                   List<Character> cpuHand,
                                                   Set<Pair<Integer, Integer>> anchors,
                                                   Pair<Integer, Integer> square,
                                                   HashSet<Character>[][] verticalCrossCheckSets,
                                                   Trie trie,
                                                   boolean transposed,
                                                   Triple<List<List<Character>>, String, Integer> bestCPUPlay) {
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
                        trie.getRoot(), verticalCrossCheckSets, trie, transposed, bestCPUPlay);
            }
            else if (boardBeforeCPUMove.get(square.getKey()).get(col - 1) != ' ')
            {
                String prefix = buildHorizontalWordForCoordinate(boardBeforeCPUMove,
                        new Pair<>(square.getKey(), col - 1)).getKey();

                ExtendRight(boardBeforeCPUMove, mutableBoard, square, prefix, cpuHand,
                        trie.getNodeForPrefix(prefix), verticalCrossCheckSets, trie, transposed, bestCPUPlay);
            }
        }
        LeftPart(boardBeforeCPUMove, mutableBoard, square,"", cpuHand,
                trie.getRoot(), verticalCrossCheckSets, k, k, trie, transposed, bestCPUPlay);
    }

    /**
     * A helper method for the recursive backtracking AI algorithm.
     * @param boardBeforeMove the scrabble board before the move
     * @param board a mutable board that represents the current state of the backtracking search
     * @param square the anchor square
     * @param partialWord the partial word formed so far
     * @param tilesRemainingInRack the tiles remaining in the CPU's hand
     * @param N the trienode currently representing the partial word
     * @param crossCheckSets the vertical cross check sets for the provided scrabble board (boardBeforeMove)
     * @param limit parameter for leftwards expansion
     * @param maxLimit maximum value of parameter for leftwards expansion
     * @param trie the trie representing the dictionary
     * @param transposed whether the board is currently a transposition of the actual model of the scrabble board
     * @param bestCPUPlay a mutable triple containing information about the cpu's best play
     */
    private static void LeftPart(List<List<Character>> boardBeforeMove, List<List<Character>> board, Pair<Integer, Integer> square, String partialWord, List<Character> tilesRemainingInRack, TrieNode N, HashSet<Character>[][] crossCheckSets, int limit, int maxLimit, Trie trie, boolean transposed, Triple<List<List<Character>>, String, Integer> bestCPUPlay)
    {
        ExtendRight(boardBeforeMove, board, square, partialWord, tilesRemainingInRack, N, crossCheckSets, trie, transposed, bestCPUPlay);
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
                    LeftPart(boardBeforeMove, board, square, partialWord + c, tilesRemainingInRack, N.getOutgoingEdges().get(c), crossCheckSets, limit - 1, maxLimit, trie, transposed, bestCPUPlay);
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
     * A helper method for the recursive backtracking AI algorithm. Extends the left part created by leftPart(...)
     *
     * @param boardBeforeCPUMove   the scrabble board before the move
     * @param board a mutable board that represents the current state of the backtracking search
     * @param square the current square to fill in
     * @param partialWord the partial word formed so far
     * @param tilesRemainingInRack tiles remaining in CPU's hand
     * @param N the trie node corresponding to partialWord
     * @param crossCheckSets the vertical cross-check sets for this board (boardBeforeCPUMove)
     * @param trie the trie representing the dictionary
     * @param transposed whether the board is currently a transposition of the actual model of the scrabble board
     * @param bestCPUPlay a mutable triple containing information about the cpu's best play
     */
    private static void ExtendRight(List<List<Character>> boardBeforeCPUMove, List<List<Character>> board, Pair<Integer, Integer> square, String partialWord, List<Character> tilesRemainingInRack, TrieNode N, HashSet<Character>[][] crossCheckSets, Trie trie, boolean transposed, Triple<List<List<Character>>, String, Integer> bestCPUPlay)
    {
        if (square.getValue() >= 15)
            return;
        if (board.get(square.getKey()).get(square.getValue()) == ' ')
        {
            if (N.isWord())
            {
                LegalMove(boardBeforeCPUMove, board, partialWord, trie, transposed, bestCPUPlay);
            }
            N.getOutgoingEdges().keySet().forEach(c -> {
                if (tilesRemainingInRack.contains(c) && crossCheckSets[square.getKey()][square.getValue()].contains(c))
                {
                    tilesRemainingInRack.remove((Character)c);
                    board.get(square.getKey()).set(square.getValue(), c);
                    ExtendRight(boardBeforeCPUMove, board, new Pair<>(square.getKey(), square.getValue() + 1), partialWord + c, tilesRemainingInRack, N.getOutgoingEdges().get(c), crossCheckSets, trie, transposed, bestCPUPlay);
                    board.get(square.getKey()).set(square.getValue(), ' ');
                    tilesRemainingInRack.add(c);
                }
            });
        }
        else
        {
            if (N.getOutgoingEdges().containsKey(board.get(square.getKey()).get(square.getValue())))
            {
                ExtendRight(boardBeforeCPUMove, board, new Pair<>(square.getKey(), square.getValue() + 1), partialWord + board.get(square.getKey()).get(square.getValue()), tilesRemainingInRack, N.getOutgoingEdges().get(board.get(square.getKey()).get(square.getValue())), crossCheckSets, trie, transposed, bestCPUPlay);
            }
        }
    }

    /**
     *
     * @param mModel the board before the CPU move
     * @param b         the state of the board (possibly transposed!) after identifying a legal move
     * @param partialWord the word to consider as a possible legal move
     * @param trie  the trie representing the dictionary
     * @param transposed whether this board is transposed
     * @param bestCPUPlay the best CPU play identified up until this point in time
     */
    private static void LegalMove( List<List<Character>> mModel,
                            List<List<Character>> b, String partialWord,
                           Trie trie,
                           boolean transposed,
                           Triple<List<List<Character>>, String, Integer> bestCPUPlay) {

        List<List<Character>> mainModel = transposed ? forEachBoardSquareAsNestedList((r, c) -> mModel.get(c).get(r)) : mModel;
        List<List<Character>> board = transposed ? forEachBoardSquareAsNestedList((r, c) -> b.get(c).get(r)) : b;

        // Get all pairs in which board differs from mainModel.
        List<Pair<Integer, Integer>> changed_coords_by_cpu = getCoordinatesListForBoard().stream().filter(x -> {
            int r = x.getKey();
            int c = x.getValue();
            return board.get(r).get(c) != mainModel.get(r).get(c);
        }).collect(Collectors.toList());

        if (!validMove(mainModel, board, trie)) {
            return;
        }

        int score;
        if (transposed)
        {
            score = scoreVertical(mainModel, board, changed_coords_by_cpu.get(0));
            score += changed_coords_by_cpu.stream().map(p -> scoreHorizontal(mainModel, board, p)).reduce((x, y) -> x+y).get();
        }
        else
        {
            score = scoreHorizontal(mainModel, board, changed_coords_by_cpu.get(0));
            score += changed_coords_by_cpu.stream().map(p -> scoreVertical(mainModel, board, p)).reduce((x, y) -> x+y).get();
        }

        if (changed_coords_by_cpu.size() == 7)
        {
            score += 50;
        }

        if (score > bestCPUPlay.getC())
        {
            bestCPUPlay.setA(forEachBoardSquareAsNestedList((r, c) -> board.get(r).get(c)));
            bestCPUPlay.setB(partialWord.concat(""));
            bestCPUPlay.setC(score);
        }
    }

}


