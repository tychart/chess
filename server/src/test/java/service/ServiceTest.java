package service;


import dataaccess.MemoryDataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.UserData;
import service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    static final Service service = new Service(new MemoryDataAccess());

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
        assertTrue(allUsers.containsKey(newUser.getUsername()));
    }

    @Test
    void registerUserFail() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        newUser.setPassword(null);
//        String jsonReturn = ;
        assertThrows(IllegalArgumentException.class, () -> service.registerUser(newUser));
//        System.out.println(jsonReturn);
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(0, allUsers.size());
        assertFalse(allUsers.containsKey(newUser.getUsername()));
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
