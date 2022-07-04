package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class RecommendationService {
    private Map<Integer, Set<Integer>> likesOfAllUsers;
    private final FilmStorage filmStorage;

    @Autowired
    public RecommendationService(@Qualifier("dbFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Set<Film> findRecommendedFilms(int id) {
        Map<Integer, Set<Integer>> intersectionsOfFilmsWithOtherUsers = findOtherUsersWithMaxIntersectionsByUserId(id);
        Set<Integer> resultFilmIds = new HashSet<>();
        Set<Film> result = new HashSet<>();

        for (var entry : intersectionsOfFilmsWithOtherUsers.entrySet()) {
            Set<Integer> userFilms = likesOfAllUsers.get(id);
            Set<Integer> otherUserFilms = entry.getValue();
            otherUserFilms.removeAll(userFilms);
            resultFilmIds.addAll(otherUserFilms);
        }
        for (var filmId : resultFilmIds) {
            result.add(filmStorage.getFilm(filmId).get());
        }
        return result;
    }

    private Map<Integer, Set<Integer>> findOtherUsersWithMaxIntersectionsByUserId(int id) {
        this.likesOfAllUsers = filmStorage.getUserLikes();
        Set<Integer> targetFilmsUserLikes = likesOfAllUsers.get(id);
        Map<Integer, Set<Integer>> result = new HashMap<>();
        Set<Integer> intersections = new HashSet<>();

        for (var fst_entry : likesOfAllUsers.entrySet()) {
            if (fst_entry.getKey().equals(id)) {
                continue;
            }
            if (fst_entry.getValue().size() > intersections.size()) {
                intersections = fst_entry.getValue();
                Set<Integer> intersectionsCopy = new HashSet<>(intersections);
                intersections.retainAll(targetFilmsUserLikes);
                if (intersections.size() > 0 && result.size() < intersections.size()) {
                    result.put(fst_entry.getKey(), intersectionsCopy);
                }
            }
        }
        return result;
    }
}


