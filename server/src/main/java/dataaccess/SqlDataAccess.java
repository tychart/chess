package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
//import exception.ResponseException;
//import model.Pet;
//import model.PetType;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

import static dataaccess.SqlDataAccess.addPersonTest;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import com.google.protobuf.ServiceException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ErrorResponse;


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
            "gameName VARCHAR(64)",
            "gameJson JSON NOT NULL"
    };

    public SqlDataAccess() {
        confirmDatabaseStructureExists();
    }

//    public Pet addPet(Pet pet) throws ResponseException {
//        var statement = "INSERT INTO pet (name, type, json) VALUES (?, ?, ?)";
//        var json = new Gson().toJson(pet);
//        var id = executeUpdate(statement, pet.name(), pet.type(), json);
//        return new Pet(id, pet.name(), pet.type());
//    }
//
//    public Pet getPet(int id) throws ResponseException {
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet WHERE id=?";
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, id);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return readPet(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return null;
//    }
//
//    public Collection<Pet> listPets() throws ResponseException {
//        var result = new ArrayList<Pet>();
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet";
//            try (var ps = conn.prepareStatement(statement)) {
//                try (var rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        result.add(readPet(rs));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return result;
//    }
//
//    public void deletePet(Integer id) throws ResponseException {
//        var statement = "DELETE FROM pet WHERE id=?";
//        executeUpdate(statement, id);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var statement = "TRUNCATE pet";
//        executeUpdate(statement);
//    }
//
//    private Pet readPet(ResultSet rs) throws SQLException {
//        var id = rs.getInt("id");
//        var json = rs.getString("json");
//        var pet = new Gson().fromJson(json, Pet.class);
//        return pet.setId(id);
//    }
//
//    private int executeUpdate(String statement, Object... params) throws ResponseException {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, NULL);
//                }
//                ps.executeUpdate();
//
//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//                return 0;
//            }
//        } catch (SQLException e) {
//            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
//        }
//    }
//
//    private final String[] createStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS  pet (
//              `id` int NOT NULL AUTO_INCREMENT,
//              `name` varchar(256) NOT NULL,
//              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
//              `json` TEXT DEFAULT NULL,
//              PRIMARY KEY (`id`),
//              INDEX(type),
//              INDEX(name)
//            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
//            """
//    };
//
//
//    private void configureDatabase() throws ResponseException {
//        DatabaseManager.createDatabase();
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }

    public void addUser(UserData user) {
        confirmDatabaseStructureExists();
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
                System.out.println("Failed to add user.");
            }

        } catch (DataAccessException | SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }


    public UserData getUser(String username) {
        confirmDatabaseStructureExists();
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
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return user;
    }


    public Map<String, UserData> getAllUsers() {
        confirmDatabaseStructureExists();
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
            System.out.println("Error retrieving user: " + e.getMessage());
        }
        return users;
    }

    public void addAuthData(AuthData authData) throws ServiceException {
        confirmDatabaseStructureExists();
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
                System.out.println("Failed to add user.");
            }

        } catch (DataAccessException | SQLException e) {
            System.out.println("Error adding authData: " + e.getMessage());
        }
    }

    public UserData authenticateUser(String authToken) throws ServiceException {
        confirmDatabaseStructureExists();
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
        confirmDatabaseStructureExists();
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
        confirmDatabaseStructureExists();
        String query = String.format("SELECT MAX(gameID) FROM %s", GAME_TABLE);

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
        confirmDatabaseStructureExists();
        String query = String.format("INSERT INTO %s (gameID, whiteUsername, blackUsername, gameName, gameJson) VALUES (?, ?, ?, ?, ?)", GAME_TABLE);

        try (PreparedStatement preparedStatement = DatabaseManager
                .getConnection()
                .prepareStatement(query)
        ) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameData.gameID());
            preparedStatement.setString(2, gameData.whiteUsername());
            preparedStatement.setString(3, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, gson.toJson(gameData.game()));

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Game added successfully.");
            } else {
                System.out.println("Failed to add game.");
            }

        } catch (DataAccessException | SQLException e) {
            throw new ServiceException("Error adding user: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws ServiceException {
        return new GameData(1, "usr1", "usr2", "game", new ChessGame());
    }

    public Map<Integer, GameData> getAllGames() {
        var map = new HashMap<Integer, GameData>();
        map.put(1, new GameData(1, "usr1", "usr2", "game", new ChessGame()));
        return map;
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

        } catch (DataAccessException | SQLException e) {
            System.err.println("Error clearing database: " + e.getMessage());
        }
    }


    public static void addPersonTest(String name, String email, int age) throws DataAccessException, SQLException {
        String insertQuery = "INSERT INTO people (name, email, age) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = DatabaseManager.getConnection().prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setInt(3, age);

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
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


    public void printDatabases() throws SQLException, DataAccessException {
        DatabaseManager.displayTable(USER_TABLE);
        DatabaseManager.displayTable(AUTH_TABLE);
        DatabaseManager.displayTable(GAME_TABLE);
    }


    public static void main(String[] args) throws DataAccessException, SQLException {
//        DatabaseManager.createTable("people");
//        addPersonTest("Tyler Chartrand", "tychart@byu.edu", 22);
        SqlDataAccess sqlDataAccess = new SqlDataAccess();
        DatabaseManager.displayTable(USER_TABLE);
        DatabaseManager.displayTable(AUTH_TABLE);
        DatabaseManager.displayTable(GAME_TABLE);
    }
}