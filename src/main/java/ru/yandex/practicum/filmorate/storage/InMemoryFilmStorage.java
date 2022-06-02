package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> storage = new ArrayList<>();
    @Override
    public Film addFilm(Film film) {
        storage.add(film);
        int filmIndex = storage.indexOf(film);
        return storage.get(filmIndex);
    }

    @Override
    public Film removeFilm(int filmId) {
        int result = findFilmIndexById(filmId);
        if (result != -1) return storage.remove(result);
        return null;
    }

    @Override
    public void removeAll() {
        storage.clear();
    }


    @Override
    public Film updateFilm(int filmId, Film film) {
        int result = findFilmIndexById(filmId);
        if (result != -1) {
            storage.set(result, film);
            return storage.get(result);
        }
        return null;
    }

    @Override
    public Film getFilm(int filmId) {
        int result = findFilmIndexById(filmId);
        if (result != -1) return storage.get(result);
        return null;
    }

    @Override
    public List<Film> getFilms(int limit, int offset) {
        return storage.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<Film> getAllFilms() {
        return storage;
    }

    private int findFilmIndexById(int id) {
        Optional<Film> result = storage.stream().filter(i -> i.getId() == id).findAny();
        return result.isEmpty() ? -1 : storage.indexOf(result.get());
    }
}
