package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

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
        Optional<User> user = Optional.ofNullable(userStorage.getUser(userId));
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilm(filmId));
        if (user.isPresent() && film.isPresent()) {
            film.get().getUsersLikedIds().add(userId);
            user.get().getFilmsLiked().add(filmId);
            return film.get();
        }
        return null;
    }

    /**
     * Remove like from Film by User ID.
     * @param userId User ID.
     * @param filmId Film ID.
     * @return Film object from which like was removed or null.
     */
    public Film removeLike(int userId, int filmId) {
        Optional<User> user = Optional.ofNullable(userStorage.getUser(userId));
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilm(filmId));
        if (user.isPresent() && film.isPresent()) {
            film.get().getUsersLikedIds().remove(userId);
            user.get().getFilmsLiked().remove(filmId);
            return film.get();
        }
        return null;
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

    public Film getFilmById(int id) { return filmStorage.getFilm(id); }

    public Film updateFilm(int id, Film film) { return filmStorage.updateFilm(id, film); }

    public Film addFilm(Film film) { return filmStorage.addFilm(film); }
}
