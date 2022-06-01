package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

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
     * @return Removed Film or null if removement is not successfull.
     */
    Film removeFilm(int filmId);

    /**
     * Update Film by ID with new Film object
     * @param filmId ID of Film object to update.
     * @param film New Film object to replace old one.
     * @return Updated Film object.
     */
    Film updateFilm(int filmId, Film film);

    /**
     * Get Film object from storage by ID.
     * @param filmId Film ID to search in storage.
     * @return Film object if film is found or null if not.
     */
    Film getFilm(int filmId);

    /**
     * Get Film objects from storage.
     * @param amount Amount of Film objects to get.
     * @param offset Offset of search from storage.
     * @return List of Film objects or null.
     */
    List<Film> getFilms(int amount, int offset);

    /**
     * Get all Films from storage.
     * @return List of all Film objects from storage.
     */
    List<Film> getAllFilms();
}
