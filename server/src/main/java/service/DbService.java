package service;

import communication.msg.server.GameListItem;
import communication.msg.server.GameStateItem;
import scrabble.Tile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;


import static org.mindrot.jbcrypt.BCrypt.*;

/**
 * Created by sujay on 8/15/17.
 */
public class DbService {

    /**
     * A connection to a MySql database.
     */
    private Connection connection;

    /**
     * Constructor. Establishes a connection to the database.
     */
    public DbService()
    {
        String dbUrl, dbname, username, password;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/dbConfig.txt"))) {
            String line;
            dbUrl = br.readLine().trim();
            dbname = br.readLine().trim();
            username = br.readLine().trim();
            password = br.readLine().trim();
            try {
                System.out.println(dbUrl);
                connection = DriverManager.getConnection(dbUrl + '/' + dbname, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
        e.printStackTrace();
        }
    }

    /**
     * Creates a new account in the system on behalf of the user.
     * @param username the provided username
     * @param password the provided unsalted password
     * @return true if a new account was created, false if not
     */
    public boolean createNewAccount(String username, String password)
    {
        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users where username = \"" + username + "\"");

            /* Check if the user with username ${username} already exists in the system */
            if (resultSet.next())
            {
                return false;
            }

            /* Generate an encrypted password using bcrypt for this user */
            String hashed = hashpw(password, gensalt());

            /* Insert a new user with {username, hashed} into the users table */
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT into users(username, password) VALUES(?,?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashed);
            preparedStatement.executeUpdate();

            statement.close();
            resultSet.close();
            preparedStatement.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
     * @return true if the game was successfully made, false otherwise
     */
    public boolean createNewGame(int p1id, String otherPlayerUsername)
    {
        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet;

            /* Find the player id for the opponent username */
            resultSet = statement.executeQuery("SELECT user_id FROM users where username = \"" + otherPlayerUsername + "\"");
            if (!resultSet.next())
                return false;
            int p2id = resultSet.getInt("user_id");

            int p1score = 0;
            int p2score = 0;

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
                    .prepareStatement("INSERT into games(p1id, p2id, p1hand, p2hand, tilebag, board, p1turn) " +
                            "VALUES (?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, p1id);
            preparedStatement.setInt(2, p2id);
            preparedStatement.setString(3, playerHand.toString());
            preparedStatement.setString(4, oppHand.toString());
            preparedStatement.setString(5, tileString.toString());
            preparedStatement.setString(6, board.toString());
            preparedStatement.setBoolean(7, true);

            preparedStatement.executeUpdate();

            resultSet.close();
            statement.close();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
                clientScore = resultSet.getInt("p2score");
                otherScore = resultSet.getInt("p1score");
                playerRack = resultSet.getString("p1hand");
            }

            String boardString = resultSet.getString("board");
            char[][] board = new char[15][15];
            for (int i = 0; i < 15; i++)
                for (int j = 0; j < 15; j++)
                {
                    board[i][j] = boardString.charAt(i * 15 + j);
                }
            String mostRecentWord = resultSet.getString("mostRecentWord");
            boolean p1turn = resultSet.getBoolean("p1turn");

            // Get opponent name from opponent id.
            Statement s2 = connection.createStatement();
            ResultSet rs2 = s2.executeQuery("SELECT username FROM users WHERE user_id = " + other_player_id);
            if (!rs2.next())
                return Optional.empty();
            String opponentName = rs2.getString(1);
            return Optional.of(new GameStateItem(opponentName, game_id, clientScore, otherScore, board, playerRack, mostRecentWord, p1turn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
