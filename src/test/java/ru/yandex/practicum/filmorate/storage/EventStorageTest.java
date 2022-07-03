package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventStorageTest {

    private final DbUserStorage dbUserStorage;
    private final DbFilmStorage dbFilmStorage;
    private final DbEventStorage dbEventStorage;

    @Test
    void addAndRemoveLikeEventsRegisteredTest() {
        Rating mpa = new Rating();
        mpa.setId(1);
        Film film = new Film();
        film.setName("film name");
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));
        film.setMpa(mpa);

        User user = new User();
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        dbFilmStorage.addFilm(film);
        dbUserStorage.addUser(user);
        dbEventStorage.addLike(1, 1);

        assertEquals(dbEventStorage.getFeed(1).size(), 1);
        assertEquals(dbEventStorage.getFeed(1).get(0).getEventType(), "LIKE");

        dbEventStorage.removeLike(1, 1);

        assertEquals(dbEventStorage.getFeed(2).size(), 0);
    }


    @Test
    void addAndDeleteFriendEventRegisteredTest() {
        User user1 = new User();
        user1.setName("Testuser1");
        user1.setLogin("testuser1");
        user1.setEmail("test@user1.com");
        user1.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        User user2 = new User();
        user2.setName("Testuser2");
        user2.setLogin("testuser2");
        user2.setEmail("test2@user2.com");
        user2.setBirthday(LocalDate.of(1990, Month.JANUARY, 1));

        dbUserStorage.addUser(user1);
        dbUserStorage.addUser(user2);
        dbEventStorage.addFriend(1, 2);

        assertEquals(dbEventStorage.getFeed(1).size(), 1);
        assertEquals(dbEventStorage.getFeed(1).get(0).getEventType(), "FRIEND");
        assertEquals(dbEventStorage.getFeed(1).get(0).getOperation(), "ADD");

        dbEventStorage.removeFriend(1, 2);

        assertEquals(dbEventStorage.getFeed(1).size(), 2);
        assertEquals(dbEventStorage.getFeed(1).get(1).getOperation(), "REMOVE");
    }

    @Test
    void addReviewTest() {
    }

    @Test
    void removeReviewTest() {
    }

    @Test
    void updateReviewTest() {
    }
}
