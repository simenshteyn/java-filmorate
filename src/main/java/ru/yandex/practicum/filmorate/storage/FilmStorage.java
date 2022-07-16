package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    /**
     * Add film to the storage.
     * @param film Film object to add.
     * @return Added Film object or null if addition not successfull.
     */
    Film addFilm(Film film);

    /**
     * Remove film from storage by ID.
     * @param filmId ID of Film object to remove.
     * @return Removed Film or Empty Optional if removement is not successfull.
     */
    Optional<Film> removeFilm(int filmId);

    /**
     * Clear storage and remove all Films.
     */
    void removeAll();

    /**
     * Update Film by ID with new Film object
     * @param filmId ID of Film object to update.
     * @param film New Film object to replace old one.
     * @return Updated Film object.
     */
    Optional<Film> updateFilm(int filmId, Film film);

    /**
     * Get Film object from storage by ID.
     * @param filmId Film ID to search in storage.
     * @return Film object if film is found or null if not.
     */
    Optional<Film> getFilm(int filmId);

    /**
     * Get Film objects from storage.
     * @param limit Amount of Film objects to get.
     * @param offset Limit amount of search from storage.
     * @return List of found Film objects.
     */
    List<Film> getFilms(int limit, int offset);

    /**
     * Get all Films from storage.
     * @return List of all Film objects from storage.
     */
    List<Film> getAllFilms();

    /**
     * Save like by user to Film
     * @param user User, who likes.
     * @param film Film to set like.
     * @return Film object.
     */
    Film saveFilmLike(User user, Film film);

    /**
     * Remove like from film.
     * @param user User, who likes Film.
     * @param film Film, which liked by User.
     * @return Film object.
     */
    Film removeFilmLike(User user, Film film);

    /**
     * Get most liked films from storage.
     * @param amount Max amount of Films to get from storage.
     * @return List of Films.
     */
    List<Film> getTopFilms(int amount, int genreId, int year);

    /**
     * Get list of Genres from storage
     * @return List of Genre objects. Could be empty.
     */
    List<Genre> getAllGenres();

    /**
     * Get Genre object by ID.
     * @param genreId ID of Genre to search in storage.
     * @return Optional of Genre.
     */
    Optional<Genre> getGenre(int genreId);

    /**
     * Get list of all Rating objects from storage.
     * @return List of Rating objects.
     */
    List<Rating> getAllRatings();

    /**
     * Get Rating object by ID.
     * @param ratingId  ID of Rating object to search in storage.
     * @return Optional of Rating object.
     */
    Optional<Rating> getRating(int ratingId);

    /**
     * Search Films by film name, director name or both.
     * @param query Text to search.
     * @param by Could be "director", "title" or both "director,title".
     * @return List of Film objects found in storage.
     */
    List<Film> searchFilmsByNameAndDirectors(String query, List<String> by);

    /**
     * Get common Films liked by two users.
     * @param userId ID of first user.
     * @param friendId ID of second user.
     * @return Optional of Film List.
     */
    Optional<List<Film>> getCommonFilmsSortedByPopularity(int userId, int friendId);

    /**
     * Get user likes for recommendation algorithm.
     * @return Map of the user's like IDs to a Set of film IDs.
     */
    Map<Integer, Set<Integer>> getUserLikes();
}
