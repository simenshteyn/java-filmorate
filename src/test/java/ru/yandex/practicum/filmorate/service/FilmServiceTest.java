package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    static private UserStorage userStorage;
    static private FilmStorage filmStorage;
    static private FilmService filmService;
    static private DbEventStorage eventStorage;
    static private User user;
    static private Film film;

    @BeforeAll
    static void beforeAll() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage, eventStorage);

        user = new User();
        user.setId(1);
        user.setName("First");
        user.setLogin("first");
        user.setEmail("first@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        film = new Film();
        film.setName("film name");
        film.setId(1);
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));

        userStorage.addUser(user);
        filmStorage.addFilm(film);
    }

    @Test
    void addLikeAndRemoveLike() {
        // Check user liked and film liked fields are empty
        assertEquals(0, filmStorage.getFilm(1).get().getUsersLikedIds().size());
        assertEquals(0, userStorage.getUser(1).get().getFilmsLiked().size());

        //Add like from user to film
        filmService.addLike(1, 1);
        assertEquals(1, filmStorage.getFilm(1).get().getUsersLikedIds().size());
        assertEquals(1, userStorage.getUser(1).get().getFilmsLiked().size());

        // Remove like from film and check
        filmService.removeLike(1, 1);
        assertEquals(0, filmStorage.getFilm(1).get().getUsersLikedIds().size());
        assertEquals(0, userStorage.getUser(1).get().getFilmsLiked().size());
    }

    @Test
    void showTopFilms() {
        Film film1 = new Film();
        film1.setUsersLikedIds(new HashSet<>(Arrays.asList(1,2,3,4,5)));
        Film film2 = new Film();
        film2.setUsersLikedIds(new HashSet<>(Arrays.asList(1,2,3)));
        Film film3 = new Film();
        film3.setUsersLikedIds(new HashSet<>(Arrays.asList(1,2,3,4)));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        List<Film> result = filmService.showTopFilms(3, 1, 2000);
        assertEquals(5, result.get(0).getUsersLikedIds().size());
        assertEquals(3, result.get(2).countUsersLiked());

    }

}
