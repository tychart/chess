package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class TempTestData {

    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/testbase";
        String username = "root";
        String password = "strong_password";

        String insertQuery = "INSERT INTO people (name, email) VALUES (?, ?)";


        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database.");
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            preparedStatement.setString(1, "value1");
            preparedStatement.setString(2, "value2");

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");


            // Execute a simple query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM people");

            // Process the results
            while (resultSet.next()) {
                System.out.println("Column1: " + resultSet.getString("name"));
                System.out.println("Column2: " + resultSet.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }

    }




}
