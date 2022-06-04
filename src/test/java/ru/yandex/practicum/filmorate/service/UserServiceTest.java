package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static private UserStorage storage;
    static private UserService service;

    @BeforeAll
    static void beforeAll() {
        storage = new InMemoryUserStorage();
        service = new UserService(storage);

        User firstUser = new User();
        firstUser.setId(1);
        firstUser.setName("First");
        firstUser.setLogin("first");
        firstUser.setEmail("first@user.com");
        firstUser.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        User secondUser = new User();
        secondUser.setId(2);
        secondUser.setName("Second");
        secondUser.setLogin("second");
        secondUser.setEmail("second@user.com");
        secondUser.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        storage.addUser(firstUser);
        storage.addUser(secondUser);
    }

    @Test
    void makeAndRemoveFriends() {
        // Check friends are null
        assertEquals(0, storage.getUser(1).get().getFriends().size());
        assertEquals(0, storage.getUser(2).get().getFriends().size());

        assertEquals("First", storage.getUser(1).get().getName());
        assertEquals("second", storage.getUser(2).get().getLogin());

        // Make and check firstUser and secondUser are friends
        List<User> result = service.makeFriends(1, 2);
        assertEquals(1, storage.getUser(2).get().getFriends().size());
        assertTrue(storage.getUser(2).get().getFriends().contains(1));
        assertEquals(1, storage.getUser(1).get().getFriends().size());
        assertTrue(storage.getUser(1).get().getFriends().contains(2));
        assertEquals(2, result.size());

        // Remove friendship of first and second user and check it
        List<User> removeResult = service.removeFriends(1, 2);
        assertEquals(0, storage.getUser(1).get().getFriends().size());
        assertEquals(0, storage.getUser(2).get().getFriends().size());
        assertEquals(2, removeResult.size());
    }

    @Test
    void showCommonFriends() {
        // Check friends are null
        assertEquals(0, storage.getUser(1).get().getFriends().size());
        assertEquals(0, storage.getUser(2).get().getFriends().size());

        assertEquals("First", storage.getUser(1).get().getName());
        assertEquals("second", storage.getUser(2).get().getLogin());

        // Add third common friend
        User thirdUser = new User();
        thirdUser.setId(3);
        thirdUser.setName("Third");
        thirdUser.setLogin("third");
        thirdUser.setEmail("third@user.com");
        thirdUser.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));
        storage.addUser(thirdUser);

        // Make friends
        service.makeFriends(1, 3);
        service.makeFriends(2, 3);

        // Third friend should be common for first and second
        List<User> result = service.showCommonFriends(1, 2);
        assertEquals(1, result.size());
    }

}