package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                String host = props.getProperty("db.host");
                int port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createTable(String tableName, String[] columns) throws DataAccessException {
        createDatabase();
//        String databaseURL = CONNECTION_URL + "/" + DATABASE_NAME;
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        createTableSQL.append(tableName);
        createTableSQL.append(" (");

        for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
            createTableSQL.append(columns[columnIndex]);
            if (columnIndex < columns.length - 1) {
                createTableSQL.append(",");
            }
        }

        createTableSQL.append(")");

        try (var connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(createTableSQL.toString());
            System.out.println("Table '" + tableName + "' created or already exists with structure: " + createTableSQL);

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    static void displayTable(String tableName) throws DataAccessException, SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        // Execute a query to retrieve all rows from the specified table
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

        // Get the ResultSet metadata
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Display column names above the data
        System.out.println("Columns in table " + tableName + ":");
        for (int i = 1; i <= columnCount; i++) {
            System.out.format("%-25s", metaData.getColumnLabel(i)); // Format each column name
        }
        System.out.println(); // New line after headers

        // Display a separator line (optional)
        System.out.println(new String(new char[columnCount * 20]).replace("\0", "-")); // Separator line

        // Display rows with formatting
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.format("%-25s", resultSet.getString(i)); // Format each column value
            }
            System.out.println();
        }

        // Close resources
        resultSet.close();
        statement.close();
        connection.close();
    }
}
