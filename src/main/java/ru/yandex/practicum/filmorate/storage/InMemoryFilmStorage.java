package ru.yandex.practicum.filmorate.storage;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> storage = new ArrayList<>();
    private int idCounter = 0;
    @Override
    public Film addFilm(Film film) {
        film.setId(++idCounter);
        storage.add(film);
        int filmIndex = storage.indexOf(film);
        return storage.get(filmIndex);
    }

    @Override
    public Optional<Film> removeFilm(int filmId) {
        int result = findFilmIndexById(filmId);
        if (result != -1) return Optional.of(storage.remove(result));
        return Optional.empty();
    }

    @Override
    public void removeAll() {
        storage.clear();
    }

    @Override
    public Optional<Film> updateFilm(int filmId, Film film) {
        int result = findFilmIndexById(filmId);
        if (result != -1) {
            storage.set(result, film);
            return Optional.of(storage.get(result));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        int result = findFilmIndexById(filmId);
        if (result != -1) return Optional.of(storage.get(result));
        return Optional.empty();
    }

    @Override
    public List<Film> getFilms(int limit, int offset) {
        return storage.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<Film> getAllFilms() {
        return storage;
    }

    @Override
    public Film saveFilmLike(User user, Film film) {
        film.getUsersLikedIds().add(user.getId());
        user.getFilmsLiked().add(film.getId());
        return film;
    }

    @Override
    public Film removeFilmLike(User user, Film film) {
        film.getUsersLikedIds().remove(user.getId());
        user.getFilmsLiked().remove(film.getId());
        return film;
    }

    @Override
    public List<Film> getTopFilms(int amount) {
        return getAllFilms().stream()
                .distinct()
                .sorted(comparing(Film::countUsersLiked).reversed().thenComparing(Film::getName))
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Optional<Genre> getGenre(int filmId) {
        return Optional.empty();
    }

    @Override
    public List<Rating> getAllRatings() {
        return null;
    }

    @Override
    public Optional<Rating> getRating(int ratingId) {
        return Optional.empty();
    }

    private int findFilmIndexById(int id) {
        Optional<Film> result = storage.stream().filter(i -> i.getId() == id).findAny();
        return result.isEmpty() ? -1 : storage.indexOf(result.get());
    }


    @Override
    public Map<Integer, Set<Integer>> getLikes() {
        throw new NotYetImplementedException();
    }
}
