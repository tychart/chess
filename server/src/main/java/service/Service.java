package service;

import com.google.protobuf.ServiceException;

import java.util.UUID;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccess;
import model.*;


public class Service {
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String clearDatabase() {
        dataAccess.clearDatabase();
        return "{}";
    }

    public String registerUser(UserData newUser) throws ServiceException {
        if (newUser.username() == null ||
                newUser.password() == null ||
                newUser.email() == null
        ) {
            throw new IllegalArgumentException("Error: Missing vital signup information, please fill out all fields");
        }

        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("Error: User already exists");
        }

        dataAccess.addUser(newUser);

        AuthData authData = new AuthData(newUser.username(), generateAuthToken());

        dataAccess.addAuthData(authData);

        LoginResponse loginResponse = new LoginResponse(newUser.username(), authData.authToken());
//        Map<String, Object> returnData = new HashMap<>();
//
//        returnData.put("username", newUser.username());
//        returnData.put("authToken", generateAuthToken());

        // Return a JSON response with username and authToken
        return gson.toJson(loginResponse);
    }

    public String loginUser(UserData loginUser) throws ServiceException {
        if (
                dataAccess.getUser(loginUser.username()) == null ||
                        !dataAccess.getUser(loginUser.username()).password().equals(loginUser.password())
        ) {
            throw new ServiceException("Error: Invalid username or password");
        }

        AuthData authData = new AuthData(loginUser.username(), generateAuthToken());

        dataAccess.addAuthData(authData);

        LoginResponse loginResponse = new LoginResponse(loginUser.username(), authData.authToken());

//        Map<String, Object> returnData = new HashMap<>();
//
//        returnData.put("username", loginUser.username());
//        returnData.put("authToken", loginUser.getAuthToken());

        // Return a JSON response with username and authToken
        return gson.toJson(loginResponse);

    }

    public String logoutUser(String authToken) throws ServiceException {
        dataAccess.deleteAuthToken(authToken);
        return "{}";
    }

    public Map<String, UserData> getAllUsers() {
        return dataAccess.getAllUsers();
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
