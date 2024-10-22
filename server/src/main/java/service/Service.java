package service;

import com.google.protobuf.ServiceException;

import java.util.UUID;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccess;
import server.UserData;


public class Service {
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String registerUser(UserData newUser) throws ServiceException {
        if (newUser.getUsername() == null ||
                newUser.getPassword() == null ||
                newUser.getEmail() == null
        ) {
            throw new IllegalArgumentException("Error: Missing vital signup information, please fill out all fields");
        }

        if (dataAccess.getUser(newUser.getUsername()) != null) {
            throw new ServiceException("Error: User already exists");
        }

        dataAccess.addUser(newUser);

        Map<String, Object> returnData = new HashMap<>();

        returnData.put("username", newUser.getUsername());
        returnData.put("authToken", generateAuthToken());

        // Return a JSON response with username and authToken
        return gson.toJson(returnData);
    }

    public String loginUser(UserData loginUser) throws ServiceException {
        if (dataAccess.getUser(loginUser.getUsername()) == null || !dataAccess.getUser(loginUser.getUsername()).getPassword().equals(loginUser.getPassword())) {
            throw new ServiceException("Error: Invalid username or password");
        }

        loginUser.setAuthToken(generateAuthToken());

        dataAccess.updateUser(loginUser);

        Map<String, Object> returnData = new HashMap<>();

        returnData.put("username", loginUser.getUsername());
        returnData.put("authToken", generateAuthToken());

        // Return a JSON response with username and authToken
        return gson.toJson(returnData);

    }

    public String logoutUser(String authToken) throws ServiceException {
        dataAccess.deleteAuthToken(authToken);
        return "";
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
