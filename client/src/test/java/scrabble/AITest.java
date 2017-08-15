package scrabble;

import javafx.util.Pair;
import util.Quadruple;
import org.junit.Before;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static scrabble.AI.*;
import static org.junit.Assert.*;

/**
 * Created by sujay on 7/27/17.
 */
public class AITest {

    private Trie trie;

    @Before
    public void setUp() throws Exception {
        // Use a pruned version of the dictionary to save time. The pruned dictionary
        // must contain all valid words that are checked as part of this test.
        trie = new Trie("src/test/resources/pruned.txt");
    }


    @org.junit.Test
    public void testCPUMove() throws Exception {
        // Positive tests
        List<Character> cpuHand = Arrays.asList('Z', 'E', 'F', 'R', 'T', 'R', 'E');
        List<List<Character>> board1 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'A', 'B', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        List<List<Character>> expectedForBoard1 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', 'F', 'E', 'Z', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'A', 'B', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input = new Quadruple<>(board1, cpuHand, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove = CPUMove(input);
        assertEquals("FEZ", cpuMove.getD().getKey());
        assertEquals(46.0, 1.0 * cpuMove.getD().getValue(), 0.1);
        assertEquals(expectedForBoard1, cpuMove.getA());

        List<Character> cpuHand2 = Arrays.asList('J', 'E', 'R', 'B', 'A', 'X', 'Z');
        List<List<Character>> board2 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'V', 'I', 'D', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        List<List<Character>> expectedForBoard2 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'Z', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'B', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'V', 'I', 'D', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input2 = new Quadruple<>(board2, cpuHand2, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove2 = CPUMove(input2);
        assertEquals("ZEBRA", cpuMove2.getD().getKey());
        assertEquals(expectedForBoard2, cpuMove2.getA());
        assertEquals(42, 1.0 * cpuMove2.getD().getValue(), 0.1);

        // Positive tests
        List<Character> cpuHand3 = Arrays.asList('A', 'X', 'R', 'J', 'K', 'L', 'D');
        List<List<Character>> board3 = Arrays.asList(
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'L', 'O', 'N', 'E', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'V', 'I', 'D', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        List<List<Character>> expectedForBoard3 = Arrays.asList(
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', 'A', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'E', 'X', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'L', 'O', 'N', 'E', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'V', 'I', 'D', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input3 = new Quadruple<>(board3, cpuHand3, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove3 = CPUMove(input3);
        assertEquals("AX", cpuMove3.getD().getKey());
        assertEquals(expectedForBoard3, cpuMove3.getA());
        assertEquals(35, 1.0 * cpuMove3.getD().getValue(), 0.1);

        List<List<Character>> board4 = expectedForBoard3;

        List<Character> cpuHand4 = Arrays.asList('C', 'A', 'T', 'E', 'R', 'S', 'A');

        List<List<Character>> expectedForBoard4 = Arrays.asList(
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', 'A', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'E', 'X', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'L', 'O', 'N', 'E', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'V', 'I', 'D', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList('C', 'A', 'R', 'A', 'T', 'E', 'S', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input4 = new Quadruple<>(board4, cpuHand4, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove4 = CPUMove(input4);
        assertEquals("CARATES", cpuMove4.getD().getKey());
        assertEquals(expectedForBoard4, cpuMove4.getA());
        assertEquals(77, 1.0 * cpuMove4.getD().getValue(), 0.1);

        List<List<Character>> board5 = expectedForBoard4;
        List<Character> cpuHand5 = Arrays.asList('T', 'O', 'S', 'E', 'S', 'R', 'S');

        List<List<Character>> expectedForBoard5 = Arrays.asList(
                Arrays.asList(' ', ' ', ' ', 'S', 'T', 'R', 'E', 'S', 'S', 'O', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'P', 'A', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'E', 'X', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'L', 'O', 'N', 'E', 'R', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', 'A', 'V', 'I', 'D', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'R', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList('C', 'A', 'R', 'A', 'T', 'E', 'S', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input5 = new Quadruple<>(board5, cpuHand5, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove5 = CPUMove(input5);
        assertEquals("STRESSOR", cpuMove5.getD().getKey());
        assertEquals(expectedForBoard5, cpuMove5.getA());
        assertEquals(77, 1.0 * cpuMove5.getD().getValue(), 0.1);

        List<List<Character>> board6 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'A', 'L', 'E', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'S', 'A', 'X', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        List<Character> cpuHand6 = Arrays.asList('V', 'E', 'L', 'E', 'A', 'U', 'E');
        List<List<Character>> expectedForBoard6 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'V', 'A', 'L', 'U', 'E', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'A', 'L', 'E', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'S', 'A', 'X', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input6 = new Quadruple<>(board6, cpuHand6, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove6 = CPUMove(input6);
        assertEquals("VALUE", cpuMove6.getD().getKey());
        assertEquals(expectedForBoard6, cpuMove6.getA());
        assertEquals(37, 1.0 * cpuMove6.getD().getValue(), 0.1);


        List<List<Character>> board7 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', 'N', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', 'P', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        List<Character> cpuHand7 = Arrays.asList('P', 'S', 'V', 'F', 'A', 'E', 'S');
        List<List<Character>> expectedForBoard7 = Arrays.asList(Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'F', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'V', 'A', 'N', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'E', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', 'S', 'A', 'P', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                Arrays.asList(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '));

        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Trie> input7 = new Quadruple<>(board7, cpuHand7, new ArrayDeque<>(), trie);
        Quadruple<List<List<Character>>, List<Character>, Queue<Character>, Pair<String, Integer>> cpuMove7 = CPUMove(input7);
        assertEquals("FAVES", cpuMove7.getD().getKey());
        assertEquals(expectedForBoard7, cpuMove7.getA());
        assertEquals(34, 1.0 * cpuMove7.getD().getValue(), 0.1);
    }
}