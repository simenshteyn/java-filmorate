package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Add like to film by User and Film IDs.
     * @param userId User ID.
     * @param filmId Film ID.
     * @return Film object to which like was added or null.
     */
    public Film addLike(int userId, int filmId) {
        User user = userStorage.getUser(userId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find film"));
        film.getUsersLikedIds().add(userId);
        user.getFilmsLiked().add(filmId);
        return film;
    }

    /**
     * Remove like from Film by User ID.
     * @param userId User ID.
     * @param filmId Film ID.
     * @return Film object from which like was removed or null.
     */
    public Film removeLike(int userId, int filmId) {
        User user = userStorage.getUser(userId).orElseThrow(()-> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find film"));
        film.getUsersLikedIds().remove(userId);
        user.getFilmsLiked().remove(filmId);
        return film;
    }

    /**
     * Show top films, more likes - more popular.
     * @param amount Size of List to show.
     * @return List of Film objects.
     */
    public List<Film> showTopFilms(int amount) {
        return filmStorage.getAllFilms().stream()
                .distinct()
                .sorted(comparing(Film::countUsersLiked).reversed().thenComparing(Film::getName))
                .limit(amount)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() { return filmStorage.getAllFilms(); }

    public Film getFilmById(int id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find film"));
    }

    public Film updateFilm(int id, Film film) {
        return filmStorage.updateFilm(id, film).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find film"));
    }

    public Film addFilm(Film film) { return filmStorage.addFilm(film); }
}
