package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmStorageTest<T extends FilmStorage> {
    T storage;

    @Test
    void addFilm() {
        // Create new film
        Film film = new Film();
        film.setName("film name");
        film.setId(1);
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
    }

    @Test
    void updateFilm() {
    }

    @Test
    void getFilm() {
    }

    @Test
    void getFilms() {
    }

    @Test
    void getAllFilms() {
    }
}