package service;


import com.google.protobuf.ServiceException;
import dataaccess.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import service.Service;
import chess.*;


import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    static DataAccess dataAccess = new SqlDataAccess();
//    static Service service = new Service(dataAccess);
    private final Gson gson = new Gson();

    @BeforeEach
    void clear() throws Exception {
        dataAccess.clearDatabase();
    }

    @Test
    void addUserSuccess() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        dataAccess.addUser(newUser);
        UserData retrievedUser = dataAccess.getUser(newUser.username());
//        System.out.println(jsonReturn);
//        Map<String, UserData> allUsers = service.getAllUsers();
//        assertEquals(1, allUsers.size());
        assertEquals(newUser, retrievedUser);
    }



}