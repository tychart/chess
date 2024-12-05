package dataaccess;

// Docker command to launch sql database:
// docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=strong_password -d mysql --max_connections=500
// docker exec -it mysql /bin/mysql -u root -p

import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;

import com.google.protobuf.ServiceException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class SqlDataAccess implements DataAccess {

    Gson gson = new Gson();

    public static final String USER_TABLE = "users";
    public static final String AUTH_TABLE = "authdata";
    public static final String GAME_TABLE = "gamedata";

    String[] userTableColumns = {
            "id INT AUTO_INCREMENT PRIMARY KEY",
            "username VARCHAR(64) UNIQUE NOT NULL",
            "password VARCHAR(100) NOT NULL",
            "email VARCHAR(128) NOT NULL",
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    };

    String[] authTableColumns = {
            "id INT AUTO_INCREMENT PRIMARY KEY",
            "username VARCHAR(64) NOT NULL",
            "authToken VARCHAR(256) UNIQUE NOT NULL"
    };

    String[] gameTableColumns = {
            "gameID INT AUTO_INCREMENT PRIMARY KEY",
            "whiteUsername VARCHAR(64)",
            "blackUsername VARCHAR(64)",
            "gameName VARCHAR(64) NOT NULL",
            "gameJson JSON NOT NULL"
    };

    public SqlDataAccess() {
        confirmDatabaseStructureExists();
    }

    public void addUser(UserData user) throws ServiceException {
//        confirmDatabaseStructureExists();
        String insertQuery = String.format("INSERT INTO %s (username, password, email) VALUES (?, ?, ?)", USER_TABLE);

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(insertQuery)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User added successfully.");
            } else {
                throw new ServiceException("Error adding user");
            }

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error adding user" + e.getMessage());
        }
    }


    public UserData getUser(String username) throws ServiceException {
//        confirmDatabaseStructureExists();
        String selectQuery = String.format("SELECT username, password, email FROM %s WHERE username = ?", USER_TABLE);
        UserData user = null;

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(selectQuery)
        ) {

            // Set the username parameter
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract data and create a UserData object
                    String userUsername = resultSet.getString("username");
                    String userPassword = resultSet.getString("password");
                    String userEmail = resultSet.getString("email");

                    user = new UserData(userUsername, userPassword, userEmail);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error retrieving user: " + e.getMessage());
        }

        return user;
    }


    public Map<String, UserData> getAllUsers() throws ServiceException {
//        confirmDatabaseStructureExists();
        String selectQuery = String.format("SELECT username, password, email FROM %s", USER_TABLE);
        Map<String, UserData> users = new HashMap<>();

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(selectQuery)
        ) {

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Extract data and create a UserData object
                    String userUsername = resultSet.getString("username");
                    String userPassword = resultSet.getString("password");
                    String userEmail = resultSet.getString("email");

                    users.put(userUsername, new UserData(userUsername, userPassword, userEmail));
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error retrieving user: " + e.getMessage());
        }
        return users;
    }

    public void addAuthData(AuthData authData) throws ServiceException {
//        confirmDatabaseStructureExists();
        String insertQuery = String.format("INSERT INTO %s (username, authToken) VALUES (?, ?)", AUTH_TABLE);

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(insertQuery)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, authData.username());
            preparedStatement.setString(2, authData.authToken());

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User added successfully.");
            } else {
                throw new ServiceException("Failed to add user.");
            }

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error adding authData: " + e.getMessage());
        }
    }

    public UserData authenticateUser(String authToken) throws ServiceException {
//        confirmDatabaseStructureExists();
        String selectQuery = String.format("SELECT username, authToken FROM %s WHERE authToken = ?", AUTH_TABLE);
        UserData returnUser = null;

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(selectQuery)
        ) {

            // Set the token parameter
            preparedStatement.setString(1, authToken);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract data and create a UserData object
                    String username = resultSet.getString("username");
                    returnUser = this.getUser(username);
                } else {
                    throw new ServiceException("Error: Provided authToken not found in the database, unauthorized");
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error retrieving user: " + e.getMessage());
        }

        return returnUser;
    }

    // Used for logging out a user
    public void deleteAuthData(String authToken) throws ServiceException {
//        confirmDatabaseStructureExists();
        String deleteQuery = String.format("DELETE FROM %s WHERE authToken = ?", AUTH_TABLE);

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(deleteQuery)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, authToken);

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User logged out successfully.");
            } else {
                throw new ServiceException("Error removing authData");
            }

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error removing authData: " + e.getMessage());
        }
    }

    public int getNextGameID() throws ServiceException {
//        confirmDatabaseStructureExists();
        String query = String.format("SELECT gameID FROM %s", GAME_TABLE);

        int expectedID = 1; // Start at the minimum game ID

        try (PreparedStatement preparedStatement = DatabaseManager.getConnection().prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Execute the query
            while (resultSet.next()) {
                int currID = resultSet.getInt("gameID");

                if (currID != expectedID) {
                    // If there is a gap, return the missing ID
                    return expectedID;
                }
                expectedID++;
            }

            // If no gaps found, return the next available ID (highest ID + 1)
            return expectedID;

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error retreving IDs: " + e.getMessage());
        }
    }

    public void addGame(GameData gameData) throws ServiceException {
//        confirmDatabaseStructureExists();

        try {
            deleteGame(gameData.gameID());
        } catch (ServiceException e) {
            // Ignore, this is just if a game is being updated
        }

        String query = String.format("INSERT INTO %s (gameID, whiteUsername, blackUsername, gameName, gameJson) VALUES (?, ?, ?, ?, ?)", GAME_TABLE);

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(query)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameData.gameID());
            preparedStatement.setString(2, gameData.whiteUsername());
            preparedStatement.setString(3, gameData.blackUsername());
            preparedStatement.setString(4, gameData.gameName());
            preparedStatement.setString(5, gson.toJson(gameData.game()));

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Game added successfully.");
            } else {
                throw new ServiceException("Failed to add game.");
            }

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error adding user: " + e.getMessage());
        }
    }

    @Override
    public void deleteGame(int gameID) throws ServiceException {
//        confirmDatabaseStructureExists();
        String query = String.format("DELETE FROM %s where gameID = ?", GAME_TABLE);

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(query)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameID);

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Game successfully deleted.");
            } else {
                throw new ServiceException("Error deleting game");
            }

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error deleting game: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws ServiceException {
//        confirmDatabaseStructureExists();
        String query = String.format("SELECT * FROM %s WHERE gameID = ?", GAME_TABLE);
        GameData outGameData = null;

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(query)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameID);

            // Execute the query and obtain the result set
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // Execute the query
                if (resultSet.next()) {
                    int retrievedGameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String gameJson = resultSet.getString("gameJson");

                    outGameData = new GameData(
                            retrievedGameID,
                            whiteUsername,
                            blackUsername,
                            gameName,
                            gson.fromJson(gameJson, ChessGame.class)
                    );

                    return outGameData;
                } else {
                    throw new ServiceException("Error: Game with ID " + gameID + " not found");
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error retreving IDs: " + e.getMessage());
        }
    }

    public Map<Integer, GameData> getAllGames() throws ServiceException {
//        confirmDatabaseStructureExists();
        String selectQuery = String.format("SELECT * FROM %s", GAME_TABLE);
        Map<Integer, GameData> games = new HashMap<>();

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(selectQuery)
        ) {

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Extract data and create a UserData object
                    int retrievedGameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String gameJson = resultSet.getString("gameJson");

                    GameData outGameData = new GameData(
                            retrievedGameID,
                            whiteUsername,
                            blackUsername,
                            gameName,
                            gson.fromJson(gameJson, ChessGame.class)
                    );

                    games.put(retrievedGameID, outGameData);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error retrieving games: " + e.getMessage());
        }
        return games;

    }

    public void clearDatabase() {
        String dropTableUsers = String.format("DROP TABLE IF EXISTS %s", USER_TABLE);
        String dropTableAuthData = String.format("DROP TABLE IF EXISTS %s", AUTH_TABLE);
        String dropTableGameData = String.format("DROP TABLE IF EXISTS %s", GAME_TABLE);

        // Establish a connection to the database and execute the queries
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            // Execute each drop table command
            statement.executeUpdate(dropTableUsers);
            System.out.println("User table dropped.");

            statement.executeUpdate(dropTableAuthData);
            System.out.println("Auth data table dropped.");

            statement.executeUpdate(dropTableGameData);
            System.out.println("Game data table dropped.");

            this.confirmDatabaseStructureExists();

        } catch (DataAccessException | SQLException e) {
            System.err.println("Error clearing database: " + e.getMessage());
        }
    }

    private void confirmDatabaseStructureExists() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTable(USER_TABLE, userTableColumns);
            DatabaseManager.createTable(AUTH_TABLE, authTableColumns);
            DatabaseManager.createTable(GAME_TABLE, gameTableColumns);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
