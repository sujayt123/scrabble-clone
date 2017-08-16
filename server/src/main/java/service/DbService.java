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

    private Connection connection;

    /**
     * Constructor. Establishes a connection to the database
     * @param driverString
     * @param jdbcURL
     */
    public DbService(String driverString, String jdbcURL)
    {
        String dbUrl, dbname, username, password;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/dbConfig.txt"))) {
            String line;
            dbUrl = br.readLine().trim();
            dbname = br.readLine().trim();
            username = br.readLine().trim();
            password = br.readLine().trim();
            try {
                connection = DriverManager.getConnection(dbUrl, username, password);
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
     * @return
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
     * @return
     */
    public boolean login(String username, String password)
    {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT password FROM users where username = \"" + username + "\"");

            /* Check if the user with username ${username} already exists in the system */
            return resultSet.next() && checkpw(password, resultSet.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
