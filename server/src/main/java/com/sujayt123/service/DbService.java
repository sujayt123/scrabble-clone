package com.sujayt123.service;

import javafx.util.Pair;
import scrabble.Tile;

import com.sujayt123.communication.msg.server.GameStateItem;
import com.sujayt123.communication.msg.server.GameListItem;
import scrabble.Trie;
import util.FunctionHelper;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static util.FunctionHelper.*;
import static scrabble.Board.*;
import static org.mindrot.jbcrypt.BCrypt.*;

/**
 * Created by sujay on 8/15/17.
 */
public class DbService {

    /**
     * A connection to a MySql database.
     */
    private Connection connection;

    private static final Trie trie = new Trie();

    /**
     * Constructor. Establishes a connection to the database.
     */
    public DbService()
    {
        String dbUrl, dbname, username, password;

        dbUrl = System.getenv("dbUrl");
        dbname = System.getenv("dbname");
        username = System.getenv("username");
        password = System.getenv("password");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println(dbUrl + '\n' + dbname + '\n' + username + '\n' + password);
            connection = DriverManager.getConnection(dbUrl + '/' + dbname, username, password);
        } catch (SQLException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new account in the system on behalf of the user.
     * @param username the provided username
     * @param password the provided unsalted password
     * @return an optional of the user id of the account if it was successfully created,
     *          optional.empty() if it was not
     */
    public Optional<Integer> createNewAccount(String username, String password)
    {
        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users where username = \"" + username + "\"");

            /* Check if the user with username ${username} already exists in the system */
            if (resultSet.next())
            {
                return Optional.empty();
            }

            /* Generate an encrypted password using bcrypt for this user */
            String hashed = hashpw(password, gensalt());

            /* Insert a new user with {username, hashed} into the users table */
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT into users(username, password) VALUES(?,?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashed);
            preparedStatement.executeUpdate();

            /* Now find the user id of the new user */
            resultSet = statement.executeQuery("SELECT user_id FROM users where username = \"" + username + "\"");
            resultSet.next();
            int user_id = resultSet.getInt(1);

            statement.close();
            resultSet.close();
            preparedStatement.close();
            return Optional.of(user_id);

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Attempts to log in to the system with the provided credentials.
     * @param username the provided username
     * @param password the provided unsalted password
     * @return an Optional of the user_id of the user if the login was successful, Optional.empty otherwise
     */
    public Optional<Integer> login(String username, String password)
    {
        Statement statement;
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users where username = \"" + username + "\"");

            /* Check if the user with username ${username} already exists in the system */
            if(resultSet.next() && checkpw(password, resultSet.getString("password")))
            {
                int user_id = resultSet.getInt("user_id");
                statement.close();
                resultSet.close();
                return Optional.of(user_id);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Deletes an account (and all associated games) in the system on behalf of the user.
     * @param user_id the user id of the verified user
     * @return true if the delete was successful, false if it was not
     */
    public boolean deleteExistingAccount(int user_id)
    {
        try {
            /* Delete this user from the users table */
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE from users where user_id = ?");
            preparedStatement.setInt(1, user_id);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an account (and all associated games) in the system on behalf of the user.
     *
     * Warning: only use this function in testing!
     *
     * @param username the username of the user to delete
     * @return true if the delete was successful, false if it was not
     */
    public boolean deleteExistingAccount(String username)
    {
        try {
            /* Delete this user from the users table */
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE from users where username = ?");
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new game involving users with usernames thisPlayerUsername and otherPlayerUsername if they exist,
     * else returns false.
     *
     * @param p1id the creating player's user_id
     * @param otherPlayerUsername the username of his/her opponent ("CPU" for cpu player)
     * @return an optional of the game id if the game was successfully made, optional.empty otherwise
     */
    public Optional<Integer> createNewGame(int p1id, String otherPlayerUsername)
    {
        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet;

            /* Find the player id for the opponent username */
            resultSet = statement.executeQuery("SELECT user_id FROM users where username = \"" + otherPlayerUsername + "\"");
            if (!resultSet.next())
                return Optional.empty();
            int p2id = resultSet.getInt("user_id");

            StringBuilder board = new StringBuilder();
            for (int i = 0 ; i < 225; i++)
            {
                board.append(' ');
            }

            Queue<Character> tiles = Tile.getTileBagForGame();
            StringBuilder playerHand = new StringBuilder();
            StringBuilder oppHand = new StringBuilder();
            StringBuilder tileString = new StringBuilder();

            for(int i = 0; i < 7; i++)
            {
                playerHand.append(tiles.poll());
                oppHand.append(tiles.poll());
            }

            tiles.forEach(tileString::append);

            /* Insert a new game with {p1id, p2id, p1score, p2score, board} into the games table */
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT into games(p1id, p2id, p1hand, p2hand, tilebag, oldBoard, board, p1turn) " +
                            "VALUES (?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, p1id);
            preparedStatement.setInt(2, p2id);
            preparedStatement.setString(3, playerHand.toString());
            preparedStatement.setString(4, oppHand.toString());
            preparedStatement.setString(5, tileString.toString());
            preparedStatement.setString(6, board.toString());
            preparedStatement.setString(7, board.toString());
            preparedStatement.setBoolean(8, true);

            preparedStatement.executeUpdate();

            statement.execute("SET sql_mode='PAD_CHAR_TO_FULL_LENGTH'");

            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
            if (!resultSet.next())
                return Optional.empty();

            int gameId = resultSet.getInt(1);

            resultSet.close();
            statement.close();
            preparedStatement.close();
            return Optional.of(gameId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Retrieves an array of the GameListItems representing games involving the player with the provided username.
     *
     * @param playerId the participating player's user id
     * @return an array of the games in which they're participating if available, Optional.empty if not in any games / error
     */
    public Optional<GameListItem[]> getGamesForPlayer(int playerId)
    {
        try {
            /* First get the player id for the provided username */
            List<GameListItem> games = new ArrayList<>();
            Statement statement = connection.createStatement();

            /* Get all details about games in which they're either the first or second player */
            ResultSet resultSet = statement.executeQuery("SELECT * FROM games where p1id = " + playerId + " OR p2id = " + playerId);

            while (resultSet.next())
            {
                int game_id = resultSet.getInt("game_id");
                int other_player_id = resultSet.getInt("p1id");
                int clientScore = resultSet.getInt("p2score");
                int otherScore = resultSet.getInt("p1score");
                if (other_player_id == playerId)
                {
                    other_player_id = resultSet.getInt("p2id");
                    clientScore = resultSet.getInt("p1score");
                    otherScore = resultSet.getInt("p2score");
                }

                // Get opponent name from opponent id.
                Statement s2 = connection.createStatement();
                ResultSet rs2 = s2.executeQuery("SELECT username FROM users WHERE user_id = " + other_player_id);
                if (!rs2.next())
                    return Optional.empty();
                String opponentName = rs2.getString("username");

                // Add elements of row to a new GameListItem and add the new GameListItem to the arraylist.
                games.add(new GameListItem(opponentName, game_id, clientScore, otherScore));
            }
            return games.isEmpty() ? Optional.empty() : Optional.of(games.toArray(new GameListItem[0]));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves the game by its id for the provided player.
     *
     * @param playerId the requesting player's user id
     * @param game_id the requested game's game id
     * @return an optional with the game state
     */
    public Optional<GameStateItem> getGameById(int playerId, int game_id)
    {
        // TODO: Test
        try {
            Statement statement = connection.createStatement();

            /* Get all details about games in which they're either the first or second player */
            ResultSet resultSet = statement.executeQuery("SELECT * FROM games where game_id = " + game_id);

            /* If the game doesn't exist, or if the user isn't certified to view the game, terminate the request */
            if (!resultSet.next() ||
                    (resultSet.getInt("p1id") != playerId && resultSet.getInt("p2id") != playerId)
                    )
                return Optional.empty();

            int other_player_id = resultSet.getInt("p1id");
            int clientScore = resultSet.getInt("p2score");
            int otherScore = resultSet.getInt("p1score");
            String playerRack = resultSet.getString("p2hand");
            if (other_player_id == playerId)
            {
                other_player_id = resultSet.getInt("p2id");
                clientScore = resultSet.getInt("p1score");
                otherScore = resultSet.getInt("p2score");
                playerRack = resultSet.getString("p1hand");
            }

            String boardString = resultSet.getString("board");
            String oldBoardString = resultSet.getString("oldBoard");
            char[][] oldBoard = new char[15][15];
            char[][] board = new char[15][15];
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    oldBoard[i][j] = oldBoardString.charAt(i * 15 + j);
                    board[i][j] = boardString.charAt(i * 15 + j);
                }
            }
            boolean clientTurn =
                    (resultSet.getBoolean("p1turn")) == (playerId == resultSet.getInt("p1id"));

            // Get opponent name from opponent id.
            Statement s2 = connection.createStatement();
            ResultSet rs2 = s2.executeQuery("SELECT username FROM users WHERE user_id = " + other_player_id);
            if (!rs2.next())
                return Optional.empty();
            String opponentName = rs2.getString(1);
            return Optional.of(new GameStateItem(opponentName, game_id, clientScore, otherScore, board, oldBoard, playerRack, clientTurn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Updates a game state in the system with the latest move.
     *
     * @param playerId the id of the player who made the move
     * @param gameId the id of the game to update
     * @param boardAfterMove the board after the player made a move
     * @return an optional of a map from playerId to the GameStateItems we should send following the update
     */
    public Optional<Map<Integer, GameStateItem>> updateGameState(int playerId, int gameId, List<List<Character>> boardAfterMove)
    {

        /*
         * To have an entry in the database consistent with the current state of the game,
         * we must:
         * 1. Retrieve player data and match one of p1 or p2 (henceforth referred to as clientPlayer) to playerId.
         * 2. Update clientPlayer's score in the table.
         * 3. Get the tile rack.
         * 4. Add tiles to clientPlayer's hand.
         * 5. Update the tile rack and clientPlayer's hand in the db.
         * 6. Update oldBoard and board in the db.
         * 7. Invert the value of p1turn in the db.
         */

        try {
            Statement statement = connection.createStatement();
            /* Get all details about games in which they're either the first or second player */
            ResultSet resultSet = statement.executeQuery("SELECT * FROM games where game_id = " + gameId);
            /* If the game doesn't exist, or if the user isn't certified to view the game, terminate the request */
            if (!resultSet.next() ||
                    (resultSet.getInt("p1id") != playerId && resultSet.getInt("p2id") != playerId)
                    )
                return Optional.empty();

            List<List<Character>> boardBeforeMove = forEachBoardSquareAsNestedList((i, j) -> {
                try {
                    return resultSet.getString("board").charAt(i * 15 + j);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            if (!validMove(boardBeforeMove, boardAfterMove, trie))
            {
                return Optional.empty();
            }

            int clientPlayer = resultSet.getInt("p1id") == playerId ? 1 : 2;

            int clientPlayerScore = resultSet.getInt("p" + clientPlayer + "score");
            clientPlayerScore += scoreMove(boardBeforeMove, boardAfterMove);

            String clientPlayerHand = resultSet.getString("p" + clientPlayer + "hand");
            String tilebag = resultSet.getString("tilebag");

            // Get all pairs in which board differs from mainModel.
            List<Pair<Integer, Integer>> changed_coords = FunctionHelper.getCoordinatesListForBoard().stream().filter(x -> {
                int r = x.getKey();
                int c = x.getValue();
                return boardBeforeMove.get(r).get(c) != boardAfterMove.get(r).get(c);
            }).collect(Collectors.toList());

            StringBuilder sb = new StringBuilder(clientPlayerHand);
            for (int i = 0; i < changed_coords.size(); i++)
            {
                char toRemove = boardAfterMove.get(changed_coords.get(i).getKey()).get(changed_coords.get(i).getValue());
                int flag = -1;
                for (int j = 0; j < sb.length(); j++)
                {
                    if (sb.charAt(j) == toRemove)
                    {
                        flag = j;
                        break;
                    }
                }
                if (flag == - 1)
                {
                    return Optional.empty();
                }
                sb.deleteCharAt(flag);
            }

            for (int i = 0; i < Math.min(changed_coords.size(), tilebag.length()); i++)
            {
                sb.append(tilebag.charAt(0));
                tilebag = tilebag.substring(1);
            }

            boolean p1turn = resultSet.getBoolean("p1turn");
            p1turn = !p1turn;

            StringBuilder oldBoardString = new StringBuilder();
            StringBuilder boardString = new StringBuilder();

            for (int i = 0; i < 15; i++)
            {
                for (int j = 0; j < 15; j++)
                {
                    oldBoardString.append(boardBeforeMove.get(i).get(j));
                    boardString.append(boardAfterMove.get(i).get(j));
                }
            }


            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE games " +
                    "SET p" + clientPlayer + "score = ?, p" + clientPlayer + "hand = ?, tilebag = ?, p1turn = ?, oldBoard = ?, board = ? " +
                    "WHERE game_id = " + gameId);
            preparedStatement.setInt(1, clientPlayerScore);
            preparedStatement.setString(2, sb.toString());
            preparedStatement.setString(3, tilebag);
            preparedStatement.setBoolean(4, p1turn);
            preparedStatement.setString(5, oldBoardString.toString());
            preparedStatement.setString(6, boardString.toString());

            preparedStatement.executeUpdate();

            int other_player_id = resultSet.getInt("p" + ((clientPlayer) % 2 + 1) + "id");
            GameStateItem clientGameStateItem, oppGameStateItem;
            // Get opponent name from opponent id.
            Statement s2 = connection.createStatement();
            ResultSet rs2 = s2.executeQuery("SELECT username FROM users WHERE user_id = " + other_player_id);
            if (!rs2.next())
                return Optional.empty();
            String opponentName = rs2.getString("username");

            rs2 = s2.executeQuery("SELECT username FROM users WHERE user_id = " + playerId);
            if (!rs2.next())
                return Optional.empty();
            String clientName = rs2.getString("username");

            String opponentPlayerHand = resultSet.getString("p" + ((clientPlayer) % 2 + 1) + "hand");
            int opponentScore = resultSet.getInt("p" + ((clientPlayer) % 2 + 1) + "score");

            char[][] oldBoard = new char[15][15];
            char[][] board = new char[15][15];
            forEachBoardSquareAsList((i, j) -> {
                oldBoard[i][j] = boardBeforeMove.get(i).get(j);
                board[i][j] = boardAfterMove.get(i).get(j);
                return null;
            });

            clientGameStateItem = new GameStateItem(opponentName, gameId, clientPlayerScore, opponentScore, board, oldBoard, sb.toString(), false);
            oppGameStateItem = new GameStateItem(clientName, gameId, opponentScore, clientPlayerScore, board, oldBoard, opponentPlayerHand, true);

            Map<Integer, GameStateItem> toSendMap = new HashMap<>();
            toSendMap.put(playerId, clientGameStateItem);
            toSendMap.put(other_player_id, oppGameStateItem);
            return Optional.of(toSendMap);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
