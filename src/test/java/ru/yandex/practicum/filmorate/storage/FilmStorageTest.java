package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class FilmStorageTest<T extends FilmStorage> {
    T storage;

    @AfterEach
    void afterEach() {
        storage.removeAll();
    }

    @Test
    void addFilm() {
        // Create new film
        Film film = new Film();
        film.setName("film name");
//        film.setId(1);
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllFilms().isEmpty());

        // Add film to storage and check it
        storage.addFilm(film);
        assertEquals(1, storage.getAllFilms().size());
    }

    @Test
    void removeFilm() {
        // Create new film
        Film film = new Film();
        film.setName("film name");
//        film.setId(42);
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllFilms().isEmpty());

        // Add film to storage and check it
        storage.addFilm(film);
        assertEquals(1, storage.getAllFilms().size());

        // Remove film by unknown ID should fail
        assertNull(storage.removeFilm(21));

        // Should return remove film with empty storage
        Film removedFilm = storage.removeFilm(film.getId());
        assertEquals("film name", removedFilm.getName());
        assertEquals(film.getId(), removedFilm.getId());
        assertTrue(storage.getAllFilms().isEmpty());
    }

    @Test
    void updateFilm() {
        // Create new film
        Film film = new Film();
        film.setName("film name");
        film.setId(33);
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));

        // Create film to update with
        Film film2 = new Film();
        film2.setName("updated film name");
        film2.setId(33);
        film2.setDescription("updated some description");
        film2.setDuration(181);
        film2.setReleaseDate(LocalDate.of(2021, Month.JANUARY, 1));


        // Check storage is empty
        assertTrue(storage.getAllFilms().isEmpty());

        // Add film to storage and check it
        storage.addFilm(film);
        assertEquals(1, storage.getAllFilms().size());

        // Update film by unknown ID should fail
        assertNull(storage.updateFilm(21, film2));

        // Should return updated with same storage size
        Film updatedFilm = storage.updateFilm(film.getId(), film2);
        assertEquals("updated film name", updatedFilm.getName());
        assertEquals(1, storage.getAllFilms().size());
    }

    @Test
    void getFilm() {
        // Create new film
        Film film = new Film();
        film.setName("film name");
        film.setId(33);
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));

        // Create second film
        Film film2 = new Film();
        film2.setName("second film name");
        film2.setId(42);
        film2.setDescription("second some description");
        film2.setDuration(181);
        film2.setReleaseDate(LocalDate.of(2021, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllFilms().isEmpty());

        // Add films to storage
        storage.addFilm(film);
        assertEquals(1, storage.getAllFilms().size());
        storage.addFilm(film2);
        assertEquals(2, storage.getAllFilms().size());

        // Get first film by ID
        Film result = storage.getFilm(1).get();
        assertEquals("film name", result.getName());

        // Get second film by ID
        Film result2 = storage.getFilm(2).get();
        assertEquals("second some description", result2.getDescription());

        // Unknown index should return null
        assertEquals(Optional.empty(), storage.getFilm(99));
    }

    @Test
    void getFilms() {
        // Create new film
        Film film = new Film();
        film.setName("film name");
        film.setId(33);
        film.setDescription("some description");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));

        // Create second film
        Film film2 = new Film();
        film2.setName("second film name");
        film2.setId(42);
        film2.setDescription("second some description");
        film2.setDuration(181);
        film2.setReleaseDate(LocalDate.of(2021, Month.JANUARY, 1));

        // Check storage is empty
        assertTrue(storage.getAllFilms().isEmpty());

        // Add films to storage
        storage.addFilm(film);
        assertEquals(1, storage.getAllFilms().size());
        storage.addFilm(film2);
        assertEquals(2, storage.getAllFilms().size());

        // Check list size with and without offset
        assertEquals(2, storage.getFilms(5, 0).size());
        assertEquals(1, storage.getFilms(5, 1).size());
        assertEquals(0, storage.getFilms(5, 5).size());

        // Check list size with limit
        assertEquals(1, storage.getFilms(1, 0).size());

    }

    @Test
    void getAllFilms() {
    }
}