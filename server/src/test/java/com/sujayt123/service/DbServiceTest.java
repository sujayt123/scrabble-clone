package com.sujayt123.service;

import com.google.gson.Gson;
import com.sujayt123.communication.msg.server.GameListItem;
import com.sujayt123.communication.msg.server.GameStateItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import scrabble.Tile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;
import static util.FunctionHelper.forEachBoardSquareAsNestedList;

/* Use the powermock library to mock the results of static function calls within implementation classes */
@RunWith( PowerMockRunner.class )
@PrepareForTest({Tile.class})
/**
 * Since DbService itself is single-threaded, all requests received in serial order are guaranteed to be
 * processed in that very order. And since all SQL actions are atomic, it is guaranteed that if one transaction
 * starts before another, it also ends before that other one. So I presume concurrency is not an issue in this test.
 *
 * (The same isn't true when running tests of multiple clients making requests to a multithreaded server.)
 *
 * Created by sujay on 8/15/17.
 */
public class DbServiceTest {

    private List<List<Character>> invalidBoard;
    private List<List<Character>> invalidBoard2;
    private List<List<Character>> validBoard;

    /* Each game is precisely specified in the testing data file. Player 1 in the following examples refers to the player who creates the game */
    /* ... In the first created game, every player's starting hand is the rack 'ABCDEFG' ... */
    private Queue<Character> gameQueue1;
    /* ... In the second created game, every player's starting hand is the rack 'EBCDEFG' ... */
    private Queue<Character> gameQueue2;
    /* ... In the third created game, player 1's starting hand is the rack 'IBCDEFH' and player 2's starting hand is the rack 'IBCDEFG' ... */
    private Queue<Character> gameQueue3;



    @Before
    public void setup()
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test/java/com/sujayt123/service/testData.json"));
            String readValue = bufferedReader.readLine();
            TestData testData = new Gson().fromJson(readValue, TestData.class);
            invalidBoard = forEachBoardSquareAsNestedList((i, j) -> testData.getInvalidBoard()[i][j]);
            invalidBoard2 = forEachBoardSquareAsNestedList((i, j) -> testData.getInvalidBoard2()[i][j]);
            validBoard = forEachBoardSquareAsNestedList((i, j) -> testData.getValidBoard()[i][j]);
            gameQueue1 = new ArrayDeque<>();
            gameQueue2 = new ArrayDeque<>();
            gameQueue3 = new ArrayDeque<>();
            for (char c : testData.getGameQueue1().toCharArray())
            {
                gameQueue1.add(c);
            }
            for (char c : testData.getGameQueue2().toCharArray())
            {
                gameQueue2.add(c);
            }
            for (char c : testData.getGameQueue3().toCharArray())
            {
                gameQueue3.add(c);
            }


        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false); // Force the test to fail if file not found
        }
    }

    @Test
    public void tests() throws Exception {
        DbService db = new DbService();

        db.deleteExistingAccount("hello");
        db.deleteExistingAccount("hello2");


        /* Set up dummy accounts */
        Optional<Integer> helloCreationSuccess, hello2CreationSuccess, helloLoginSuccess, hello2LoginSuccess;

        assertTrue((helloCreationSuccess = db.createNewAccount("hello", "world")).isPresent());
        assertTrue((hello2CreationSuccess = db.createNewAccount("hello2", "world2")).isPresent());
        assertFalse(db.createNewAccount("CPU", "password").isPresent());
        assertFalse(db.createNewAccount("hello", "password").isPresent());
        assertFalse(db.createNewAccount("hello2", "password").isPresent());

        /* Log in to dummy accounts */
        assertTrue((helloLoginSuccess = db.login("hello", "world")).isPresent());
        assertTrue((hello2LoginSuccess = db.login("hello2", "world2")).isPresent());
        assertFalse(db.login("hello", "world2").isPresent());
        assertFalse(db.login("hello2", "world").isPresent());

        /* Logging in should return the same user ids */
        assertEquals(helloCreationSuccess.get(), helloLoginSuccess.get());
        assertEquals(hello2CreationSuccess.get(), hello2LoginSuccess.get());

        mockStatic(Tile.class);

        when(Tile.getTileBagForGame()).thenReturn(gameQueue1, gameQueue2, gameQueue3);
        when(Tile.scoreCharacter(anyChar())).thenCallRealMethod();

        /* Create games involving dummy players */
        Optional<Integer> game1, game2, game3;
        assertFalse(db.createNewGame(helloLoginSuccess.get(), "notaRealPlayer").isPresent());
        assertTrue((game1 = db.createNewGame(helloLoginSuccess.get(), "CPU")).isPresent());
        assertTrue((game2 = db.createNewGame(hello2LoginSuccess.get(), "CPU")).isPresent());
        assertTrue((game3 = db.createNewGame(hello2LoginSuccess.get(), "hello")).isPresent());
        verifyStatic(times(3));
        Tile.getTileBagForGame();

        Optional<GameListItem[]> gamesForHello = db.getGamesForPlayer(helloLoginSuccess.get());
        Optional<GameListItem[]> gamesForHello2 = db.getGamesForPlayer(hello2LoginSuccess.get());
        assertTrue(gamesForHello.isPresent());
        assertTrue(gamesForHello2.isPresent());

        /* Assert that every game for the player "Hello" is against one of hello2 and cpu */
        Arrays.asList(gamesForHello.get()).forEach(x -> assertTrue(Arrays.asList("hello2", "CPU").contains(x.getOpponentName())));
        assertEquals(gamesForHello.get().length, 2);

        /* Assert that every game for the player "Hello2" is against one of hello and cpu */
        Arrays.asList(gamesForHello2.get()).forEach(x -> assertTrue(Arrays.asList("hello", "CPU").contains(x.getOpponentName())));
        assertEquals(gamesForHello2.get().length, 2);

        /* Test that hello's games were created and retrieved correctly */
        for (int k = 0; k < 2; k++)
        {
            Optional<GameStateItem> gameStateItemHello = db.getGameById(helloLoginSuccess.get(), gamesForHello.get()[k].getGame_id());
            assertTrue(gameStateItemHello.isPresent());
            for (int i = 0; i < 15; i++)
            {
                for (int j = 0; j < 15; j++)
                {
                    assertEquals(gameStateItemHello.get().getOldBoard()[i][j], ' ');
                    assertEquals(gameStateItemHello.get().getBoard()[i][j], ' ');
                }
            }

        /* The logic of the createGame function ensures that the creating player goes first. So
         * if we're checking the game against the CPU, we go first. If we're checking the game against
          * hello2, hello2 goes first because he created the game. */

            if (gameStateItemHello.get().getOpponentName().equals("CPU"))
            {
                assertTrue(gameStateItemHello.get().isClientTurn());
                assertEquals(gameStateItemHello.get().getClientHand(), "ABCDEFG");
            }
            else
            {
                assertFalse(gameStateItemHello.get().isClientTurn());
                assertEquals(gameStateItemHello.get().getClientHand(), "IBCDEFG");
            }
        }

        /* Test that hello2's games were created and retrieved correctly */
        for (int k = 0; k < 2; k++)
        {
            Optional<GameStateItem> gameStateItemHello2 = db.getGameById(hello2LoginSuccess.get(), gamesForHello2.get()[k].getGame_id());
            assertTrue(gameStateItemHello2.isPresent());
            for (int i = 0; i < 15; i++)
            {
                for (int j = 0; j < 15; j++)
                {
                    assertEquals(gameStateItemHello2.get().getOldBoard()[i][j], ' ');
                    assertEquals(gameStateItemHello2.get().getBoard()[i][j], ' ');
                }
            }

        /* The logic of the createGame function ensures that the creating player goes first. So
         * if we're checking the game against the CPU, we go first. If we're checking the game against
          * hello, we go first because he created the game. */

            if (gameStateItemHello2.get().getOpponentName().equals("CPU"))
            {
                assertTrue(gameStateItemHello2.get().isClientTurn());
                assertEquals(gameStateItemHello2.get().getClientHand(), "EBCDEFG");
            }
            else
            {
                assertTrue(gameStateItemHello2.get().isClientTurn());
                assertEquals(gameStateItemHello2.get().getClientHand(), "IBCDEFH");
            }
        }

        /* Now attempt making an update to hello's game against the CPU */
        for (int k = 0; k < 2; k++)
        {
            final int gameId = gamesForHello.get()[k].getGame_id();
            GameStateItem gameStateItemHello = db.getGameById(helloLoginSuccess.get(), gameId).get();
            if (gameStateItemHello.getOpponentName().equals("CPU"))
            {
                // It's "hello"'s turn against CPU.
                assertFalse(db.updateGameState(helloLoginSuccess.get(), gameId, invalidBoard).isPresent());
                assertFalse(db.updateGameState(helloLoginSuccess.get(), gameId, invalidBoard2).isPresent());

                Optional<Map<Integer, GameStateItem>> updateRetVal;
                assertTrue((updateRetVal = db.updateGameState(helloLoginSuccess.get(), gameId, validBoard)).isPresent());

                /* Assert some things about the game vs the CPU now that the move has been made */
                updateRetVal.get().forEach((key, value) -> {
                    // Certain things must be true about "hello's" view of the game.
                    if (key.equals(helloLoginSuccess.get()))
                    {
                        assertEquals(value.getOpponentName(), "CPU");
                        assertTrue(value.getClientHand().equals("CDEFGHH"));
                        assertEquals(value.getClientScore(), 8);
                        assertEquals(value.getOpponentScore(), 0);
                        assertEquals(value.getOldBoard()[7][6], ' ');
                        assertEquals(value.getOldBoard()[7][7], ' ');
                        assertEquals(value.getBoard()[7][6], 'A');
                        assertEquals(value.getBoard()[7][7], 'B');
                        assertFalse(value.isClientTurn());
                    }
                    // Certain things must be true about "cpu's" view of the game.
                    else
                    {
                        assertEquals(value.getOpponentName(), "hello");
                        assertTrue(value.getClientHand().equals("ABCDEFG"));
                        assertEquals(value.getOpponentScore(), 8);
                        assertEquals(value.getClientScore(), 0);
                        assertEquals(value.getOldBoard()[7][6], ' ');
                        assertEquals(value.getOldBoard()[7][7], ' ');
                        assertEquals(value.getBoard()[7][6], 'A');
                        assertEquals(value.getBoard()[7][7], 'B');
                        assertTrue(value.isClientTurn());
                    }
                });
            }
        }


        /* Remove dummy accounts */
        db.deleteExistingAccount(helloLoginSuccess.get());
        db.deleteExistingAccount(hello2LoginSuccess.get());

    }
}