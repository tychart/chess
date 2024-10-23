package service;


import com.google.protobuf.ServiceException;
import dataaccess.MemoryDataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    static final Service service = new Service(new MemoryDataAccess());
    private final Gson gson = new Gson();

    @BeforeEach
    void clear() throws Exception {
        service.clearDatabase();
    }

    @Test
    void registerUser() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        String jsonReturn = service.registerUser(newUser);
        System.out.println(jsonReturn);
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.containsKey(newUser.username()));
    }

    @Test
    void registerUserFail() throws Exception {
        UserData newUser = new UserData("username", null, "myemail");
        assertThrows(IllegalArgumentException.class, () -> service.registerUser(newUser));
//        System.out.println(jsonReturn);
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(0, allUsers.size());
        assertFalse(allUsers.containsKey(newUser.username()));
    }

    @Test
    void registerUserFailDuplicate() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        service.registerUser(newUser);
        assertThrows(ServiceException.class, () -> service.registerUser(newUser));
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.containsKey(newUser.username()));
    }

    @Test
    void loginUserSuccess() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);

        UserData loginUser = new UserData("username", "password", null);
        String jsonReturn = service.loginUser(loginUser);

        // Assert that the auth token is present in the JSON response
        Map<String, Object> response = gson.fromJson(jsonReturn, HashMap.class);
        assertNotNull(response.get("authToken"));
    }

    @Test
    void loginUserInvalidUsername() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);

        UserData loginUser = new UserData("invalid_username", "password", null);
        assertThrows(ServiceException.class, () -> service.loginUser(loginUser));
    }

    @Test
    void loginUserInvalidPassword() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);

        UserData loginUser = new UserData("username", "invalid_password", null);
        assertThrows(ServiceException.class, () -> service.loginUser(loginUser));
    }

    @Test
    void logoutUserSuccess() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);
        String loggedInUserJson = service.loginUser(user);
        LoginResponse logedInUser = gson.fromJson(loggedInUserJson, LoginResponse.class);


        String authToken = logedInUser.authToken();
        String jsonReturn = service.logoutUser(authToken);

        assertEquals("{}", jsonReturn);
    }

    @Test
    void logoutUserInvalidAuthToken() throws Exception {
        assertThrows(ServiceException.class, () -> service.logoutUser("invalid_auth_token"));
    }

//    @Test
//    void listPets() throws ResponseException {
//        List<Pet> expected = new ArrayList<>();
//        expected.add(service.addPet(new Pet(0, "joe", PetType.FISH)));
//        expected.add(service.addPet(new Pet(0, "sally", PetType.CAT)));
//        expected.add(service.addPet(new Pet(0, "fido", PetType.DOG)));
//
//        var actual = service.listPets();
//        assertIterableEquals(expected, actual);
//    }
//
//    @Test
//    void deletePet() throws ResponseException {
//        List<Pet> expected = new ArrayList<>();
//        var pet = service.addPet(new Pet(0, "joe", PetType.FISH));
//        expected.add(service.addPet(new Pet(0, "sally", PetType.CAT)));
//        expected.add(service.addPet(new Pet(0, "fido", PetType.DOG)));
//
//        service.deletePet(pet.id());
//        var actual = service.listPets();
//        assertIterableEquals(expected, actual);
//    }
//
//    @Test
//    void deleteAllPets() throws ResponseException {
//        service.addPet(new Pet(0, "joe", PetType.FISH));
//        service.addPet(new Pet(0, "sally", PetType.CAT));
//        service.addPet(new Pet(0, "fido", PetType.DOG));
//
//        service.deleteAllPets();
//        assertEquals(0, service.listPets().size());
//    }
//
//    @Test
//    void noDogsWithFleas() {
//        assertThrows(ResponseException.class, () ->
//                service.addPet(new Pet(0, "fleas", PetType.DOG)));
//    }
}
