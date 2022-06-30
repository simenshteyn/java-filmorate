package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

        String sqlCheck = "SELECT director_id FROM film_directors WHERE director_id = ?";
        List<Integer> listFind = jdbcTemplate.query(sqlCheck, (rs, rowNum) -> rs.getInt(1), directorId);
        if (listFind.size() == 0) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find director's id");
        }

        String sql = "SELECT f.film_id, f.film_name, f.film_description, f.film_release_date, f.film_duration, f.film_rating_id, d.director_id, d.director_name FROM films AS f " +
                "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE fd.director_id = ?";

        List<Film> film = jdbcTemplate.query(sql, new MapRowToFilm(), directorId);

        for (Film x : film) {
            String sqlQueryGenres = "SELECT g.genre_id, g.genre_name FROM film_genres AS fg " +
                    "JOIN genres AS g ON g.genre_id = fg.genre_id WHERE film_id = ?";
            List<Genre> genres = jdbcTemplate.query(sqlQueryGenres, new MapRowToGenre(), x.getId());
            if (genres.size() > 0) {
                x.setGenres(new HashSet<>(genres));
            }
        }

        switch (sortBy) {
            case "year":
                return film.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
            case "likes":
                return film.stream().sorted(Comparator.comparing(o -> o.getUsersLikedIds().size())).collect(Collectors.toList());
        }
        return film;
    }

    public List<Director> getAllDirectors() {
        String sql = "SELECT director_id, director_name FROM directors";

        return jdbcTemplate.query(sql, new MapRowToDirector());
    }

    public Director getDirectorById(int id) {
        String sqlCheck = "SELECT director_id FROM directors WHERE director_id = ?";
        List<Integer> listFind = jdbcTemplate.query(sqlCheck, (rs, rowNum) -> rs.getInt(1), id);
        if (listFind.size() == 0) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find director's id");
        }

        String sql = "SELECT director_id, director_name FROM directors WHERE director_id = ?";

        return jdbcTemplate.queryForObject(sql, new MapRowToDirector(), id);
    }

    public Director addDirector(Director director) {
        String sql = "INSERT INTO directors (director_name) " +
                "VALUES (?)";

        jdbcTemplate.update(sql, director.getName());
        return jdbcTemplate.queryForObject("SELECT director_id, director_name FROM directors WHERE director_name = ?", new MapRowToDirector(), director.getName());
    }

    public Director updateDirector(Director director) {
        String sqlCheck = "SELECT director_id FROM directors WHERE director_id = ?";
        List<Integer> listFind = jdbcTemplate.query(sqlCheck, (rs, rowNum) -> rs.getInt(1), director.getId());
        if (listFind.size() == 0) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find director's id");
        }

        String sql = "UPDATE directors SET " +
                "director_id = ?, director_name = ?";
        jdbcTemplate.update(sql,
                director.getId(),
                director.getName());

        return getDirectorById(director.getId());
    }

    public void deleteDirectorById(int id) {
        String sqlDeleteDirector = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlDeleteDirector, id);

        String sqlDeleteFilmDirector = "DELETE FROM film_directors WHERE director_id = ?";
        jdbcTemplate.update(sqlDeleteFilmDirector, id);
    }
}
