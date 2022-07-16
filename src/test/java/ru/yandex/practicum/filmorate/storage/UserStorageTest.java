package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class UserStorageTest<T extends UserStorage> {
    T storage;

    @AfterEach
    void afterEach() { storage.removeAll(); }

    @Test
    void addUser() {
        // Create new user
        User user = new User();
        user.setId(1);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllUsers().isEmpty());

        // Add user to storage and check it
        storage.addUser(user);
        assertEquals(1, storage.getAllUsers().size());
    }

    @Test
    void removeUser() {
        // Create new user
        User user = new User();
        user.setId(42);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllUsers().isEmpty());

        // Add user to storage and check it
        storage.addUser(user);
        assertEquals(1, storage.getAllUsers().size());

        // Remove user and check it
        Optional<User> removedUser = storage.removeUser(user.getId());
        assertEquals("Testuser", removedUser.get().getName());
        assertTrue(storage.getAllUsers().isEmpty());
    }

    @Test
    void updateUser() {
        // Create new user
        User user = new User();
        user.setId(42);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllUsers().isEmpty());

        // Add user to storage and check it
        storage.addUser(user);
        assertEquals(1, storage.getAllUsers().size());

        // Update user with new name: unknown id should fail
        user.setName("Updated Testuser");
        assertEquals(Optional.empty(), storage.updateUser(41, user));
        storage.updateUser(user.getId(), user);
        System.out.println(user);
        assertEquals("Updated Testuser", storage.getUser(user.getId()).get().getName());
    }

    @Test
    void getUser() {
        // Create new user
        User user = new User();
        user.setId(42);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllUsers().isEmpty());

        // Add user to storage and check it
        storage.addUser(user);
        assertEquals(1, storage.getAllUsers().size());

        // Wrong ID should return Empty Optional
        assertEquals(Optional.empty(), storage.getUser(21));

        // Right ID should return User
        assertEquals("Testuser", storage.getUser(user.getId()).get().getName());
    }

    @Test
    void getUserFriends() {
        // Create new user
        User user = new User();
        user.setId(42);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.getFriends().add(2);
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        // Create second new user
        User user2 = new User();
        user2.setId(13);
        user2.setName("Seconduser");
        user2.setLogin("seconduser");
        user2.setEmail("second@user.com");
        user2.getFriends().add(1);
        user2.setBirthday(LocalDate.of(1980, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllUsers().isEmpty());

        // Add user to storage and check it
        storage.addUser(user);
        storage.addUser(user2);
        assertEquals(2, storage.getAllUsers().size());

        // Check both are friends
        assertEquals("Seconduser", storage.getUserFriends(user.getId()).get().get(0).getName());
        assertEquals("Testuser", storage.getUserFriends(user2.getId()).get().get(0).getName());
    }

    @Test
    void getUsers() {
        // Create new user
        User user = new User();
        user.setId(42);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        // Create second new user
        User user2 = new User();
        user2.setId(13);
        user2.setName("Seconduser");
        user2.setLogin("seconduser");
        user2.setEmail("second@user.com");
        user2.setBirthday(LocalDate.of(1980, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllUsers().isEmpty());

        // Add user to storage and check it
        storage.addUser(user);
        storage.addUser(user2);
        assertEquals(2, storage.getAllUsers().size());

        // Check offset and limit a working
        assertEquals(2, storage.getUsers(5,0).size());
        assertEquals(1, storage.getUsers(5, 1).size());
        assertEquals(1, storage.getUsers(1, 0).size());
    }

    @Test
    void getAllUsers() {
    }
}
