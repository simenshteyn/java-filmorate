package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToDirector;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToFilm;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToGenre;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Repository
public class DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> getSortedFilmsByDirectors(int directorId, String sortBy) {
        getDirectorById(directorId);

        String sql = "SELECT f.film_id, f.film_name, f.film_description, f.film_release_date, f.film_duration, f.film_rating_id, d.director_id, d.director_name " +
                       "FROM films AS f " +
                            "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
                            "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                      "WHERE fd.director_id = ?";
        List<Film> films = jdbcTemplate.query(sql, new MapRowToFilm(), directorId);

        String sqlQueryGenres = "SELECT g.genre_id, g.genre_name " +
                                  "FROM film_genres AS fg " +
                                       "JOIN genres AS g " +
                                            "ON g.genre_id = fg.genre_id " +
                                 "WHERE film_id = ?";
        films.forEach(f -> {
            List<Genre> genres = jdbcTemplate.query(sqlQueryGenres, new MapRowToGenre(), f.getId());
            if (genres.size() > 0) f.setGenres(new HashSet<>(genres));
        });

        switch (sortBy) {
            case "year":
                return films.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
            case "likes":
                return films.stream().sorted(Comparator.comparing(o -> o.getUsersLikedIds().size())).collect(Collectors.toList());
            default:
                return films;
        }
    }

    public List<Director> getAllDirectors() {
        String sql = "SELECT director_id, director_name FROM directors";
        return jdbcTemplate.query(sql, new MapRowToDirector());
    }

    public Director getDirectorById(int directorId) {
        Director director;
        try {
            String sqlQuery = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
            director = jdbcTemplate.queryForObject(sqlQuery, new MapRowToDirector(), directorId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find director by id");
        }
        return director;
    }

    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        int directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
        String sqlQuery = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, new MapRowToDirector(), directorId);
    }

    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";

        jdbcTemplate.update(sql, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    public void deleteDirectorById(int directorId) {
        String sqlDeleteDirector = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlDeleteDirector, directorId);

        String sqlDeleteFilmDirector = "DELETE FROM film_directors WHERE director_id = ?";
        jdbcTemplate.update(sqlDeleteFilmDirector, directorId);
    }
}
