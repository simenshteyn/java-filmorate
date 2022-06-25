package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Qualifier("dBFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films (film_name, film_description, film_release_date, film_duration) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            return stmt;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, filmId);
    }

    @Override
    public Optional<Film> removeFilm(int filmId) {
        String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films WHERE film_id = ?";
        Optional<Film> result = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, filmId));
//        if (result.isEmpty()) return null;
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        return result;
    }

    @Override
    public void removeAll() {
        String sqlQuery = "DELETE FROM films";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Optional<Film> updateFilm(int filmId, Film film) {
        try {
            String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films WHERE film_id = ?";
            Film result = jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        String sqlQuery = "UPDATE films SET film_name = ?, film_description = ?, film_release_date = ?, film_duration = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        String sqlQuerySecondSearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films WHERE film_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuerySecondSearch, this::mapRowToFilm, filmId));
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        try {
            String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films WHERE film_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getFilms(int limit, int offset) {
        String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit, offset);
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film saveFilmLike(User user, Film film) {
        String sqlQuery = "INSERT INTO films_liked (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
        return film;
    }

    @Override
    public Film removeFilmLike(User user, Film film) {
        String sqlQuery = "DELETE FROM films_liked WHERE (user_id, film_id) IN ((?, ?))";
        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film result = new Film();
        result.setId(resultSet.getInt("film_id"));
        result.setName(resultSet.getString("film_name"));
        result.setDescription(resultSet.getString("film_description"));
        result.setReleaseDate(resultSet.getDate("film_release_date").toLocalDate());
        result.setDuration(resultSet.getInt("film_duration"));
        return result;
    }

//    private Film getFilmById(int id) {
//        return getFilm(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
//    }
}
