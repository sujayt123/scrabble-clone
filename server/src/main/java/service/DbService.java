package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;


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
     * @return true if the login was successful, false if it was not
     */
    public boolean login(String username, String password)
    {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT password FROM users where username = \"" + username + "\"");

            /* Check if the user with username ${username} already exists in the system */
            boolean retVal = resultSet.next() && checkpw(password, resultSet.getString(1));
            statement.close();
            resultSet.close();
            return retVal;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an account (and all associated games) in the system on behalf of the user.
     * @param username the provided username
     * @param password the provided unsalted password
     * @return true if the delete was successful, false if it was not
     */
    public boolean deleteExistingAccount(String username, String password)
    {
        try {
            /* Does the user have the appropriate credentials to delete the account? */
            if (!login(username, password))
                return false;

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
     * @param thisPlayerUsername
     * @param otherPlayerUsername
     * @return
     */
    public boolean createNewGame(String thisPlayerUsername, String otherPlayerUsername)
    {
        try {
            /* First find the player id for this player username */
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT user_id FROM users where username = \"" + thisPlayerUsername + "\"");

            if (!resultSet.next())
                return false;
            int p1id = resultSet.getInt(1);

            resultSet.close();

            /* Next find the player id for the opponent username */
            resultSet = statement.executeQuery("SELECT user_id FROM users where username = \"" + otherPlayerUsername + "\"");
            if (!resultSet.next())
                return false;
            int p2id = resultSet.getInt(1);

            int p1score = 0;
            int p2score = 0;

            StringBuilder board = new StringBuilder();
            for (int i = 0 ; i < 225; i++)
            {
                board.append(' ');
            }

            /* Insert a new game with {p1id, p2id, p1score, p2score, board} into the games table */
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT into games(p1id, p2id, p1score, p2score, board, p1turn) VALUES (?,?,?,?,?,?)");
            preparedStatement.setInt(1, p1id);
            preparedStatement.setInt(2, p2id);
            preparedStatement.setInt(3, p1score);
            preparedStatement.setInt(4, p2score);
            preparedStatement.setString(5, board.toString());
            preparedStatement.setBoolean(6, true);

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

}
