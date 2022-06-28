package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToDirector;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToFilm;

import java.util.Comparator;
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
        String sql = "SELECT * FROM films AS f JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "JOIN directors AS d ON fd.director_id = d.director_id WHERE director_id = ?";

        List<Film> film = jdbcTemplate.query(sql, new MapRowToFilm(), directorId);

        if(sortBy.equals("year")) {
           return film.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            return film.stream().sorted(Comparator.comparing(o -> o.getUsersLikedIds().size())).collect(Collectors.toList());
        }

        return film;
    }

    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";

        return jdbcTemplate.query(sql, new MapRowToDirector());
    }

    public Director getDirectorById(int id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";

        return jdbcTemplate.queryForObject(sql, new MapRowToDirector(), id);
    }

    public Director addDirector(Director director) {
        String sql = "INSERT INTO directors (director_id, director_name) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql,
                director.getId(),
                director.getName());

        return getDirectorById(director.getId());
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
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
