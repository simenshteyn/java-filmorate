package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("dBFilmStorage")
public class DbFilmStorage implements FilmStorage {
    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Optional<Film> removeFilm(int filmId) {
        return Optional.empty();
    }

    @Override
    public void removeAll() {

    }

    @Override
    public Optional<Film> updateFilm(int filmId, Film film) {
        return Optional.empty();
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        return Optional.empty();
    }

    @Override
    public List<Film> getFilms(int limit, int offset) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }
}
