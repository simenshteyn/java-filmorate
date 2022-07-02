package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToDirector;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToFilm;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToFilmDirector;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();

        if (film.getGenres() != null) {
            SimpleJdbcInsert secondSimpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_genres")
                    .usingColumns("film_id", "genre_id");
            film.getGenres().forEach(g -> secondSimpleJdbcInsert.execute(Map.of("film_id", filmId,
                    "genre_id", g.getId())));
        }

        if (film.getDirectors().size() != 0) {
            String sqlFilm = "INSERT INTO film_directors (film_id, director_id) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlFilm,
                    filmId,
                    film.getDirectors().get(0).getId());
        }

        return getFilmById(filmId);
    }

    @Override
    public Optional<Film> removeFilm(int filmId) {
        String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration " +
                "FROM films WHERE film_id = ?";
        Optional<Film> result = Optional.ofNullable(
                jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, filmId)
        );
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
            String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration, film_rating_id " +
                    "FROM films WHERE film_id = ?";
            jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        String sqlQuery = "UPDATE films SET film_name = ?, film_description = ?, film_release_date = ?, film_duration = ?, film_rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        if (film.getDirectors().size() != 0) {
            String sqlCheck = "SELECT film_id FROM film_directors WHERE film_id = ?";
            List<Integer> listFind = jdbcTemplate.query(sqlCheck, (rs, rowNum) -> rs.getInt(1), filmId);
            if (listFind.size() == 0) {
                String sqlDir = "INSERT INTO film_directors (film_id, director_id) " +
                        "VALUES (?, ?)";
                jdbcTemplate.update(sqlDir,
                        filmId,
                        film.getDirectors().get(0).getId());
            } else {
                String sqlDirector = "UPDATE film_directors SET film_id = ?, director_id = ? WHERE film_id = ?";
                jdbcTemplate.update(sqlDirector, filmId, film.getDirectors().get(0), filmId);
            }
        }

        if (film.getGenres() != null) {
            String sqlQueryGenresRemove = "DELETE FROM film_genres WHERE film_id = ?";
            jdbcTemplate.update(sqlQueryGenresRemove, filmId);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_genres")
                    .usingColumns("film_id", "genre_id");
            film.getGenres().forEach(g -> simpleJdbcInsert.execute(Map.of("film_id", filmId, "genre_id", g.getId())));
        }

        Optional<Film> result = getFilm(filmId);
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) result.get().setGenres(new HashSet<>());
        }

        if (film.getDirectors() != null) {
            if (film.getDirectors().isEmpty()) {
                String sqlDeleteDirector = "DELETE FROM film_directors WHERE film_id = ?";
                jdbcTemplate.update(sqlDeleteDirector, film.getId());
                result.get().setDirectors(null);
            }
        }
        return result;
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        try {
            String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration, film_rating_id " +
                    "FROM films WHERE film_id = ?";
            Optional<Film> result = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId));

            if (result.isPresent()) {
                Film film = result.get();

                String sqlQueryGenres = "SELECT g.genre_id, g.genre_name FROM film_genres AS fg " +
                        "JOIN genres AS g ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
                List<Genre> genres = jdbcTemplate.query(sqlQueryGenres, this::mapRowToGenre, filmId);
                if (genres.size() > 0) {
                    film.setGenres(new HashSet<>());
                    genres.forEach(g -> film.getGenres().add(g));
                }

                String sqlQueryRating = "SELECT rating_id, rating_name FROM ratings WHERE rating_id = ?";
                Optional<Rating> rating = Optional.ofNullable(
                        jdbcTemplate.queryForObject(sqlQueryRating, this::mapRowToRating, film.getMpa().getId())
                );
                rating.ifPresent(film::setMpa);

                String sqlQueryDirectors = "SELECT d.director_id, d.director_name FROM film_directors AS fd JOIN directors AS d ON fd.director_id = d.director_id WHERE film_id = ?";
                List<Director> directors = jdbcTemplate.query(sqlQueryDirectors, new MapRowToDirector(), filmId);
                if (directors.size() > 0) {
                    film.setDirectors(new ArrayList<>());
                    directors.forEach(g -> film.getDirectors().add(g));
                }
                return Optional.of(film);
            }
            return result;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getFilms(int limit, int offset) {
        String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration, film_rating_id FROM films LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit, offset);
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM films AS f LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id LEFT JOIN directors AS d ON fd.director_id = d.director_id";
        return jdbcTemplate.query(sqlQuery, new MapRowToFilm());
    }

    @Override
    public Film saveFilmLike(User user, Film film) {
        String sqlQuery = "INSERT INTO films_liked (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public Film removeFilmLike(User user, Film film) {
        String sqlQuery = "DELETE FROM films_liked WHERE (user_id, film_id) IN ((?, ?))";
        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getTopFilms(int amount) {
        String sqlQuery = "SELECT f.film_id, film_name, film_description, film_release_date, film_duration, film_rating_id " +
                            "FROM films AS f " +
                                 "LEFT JOIN films_liked AS fl " +
                                      "ON f.film_id = fl.film_id " +
                           "GROUP BY f.film_id " +
                           "ORDER BY COUNT(DISTINCT fl.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, amount);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT genre_id, genre_name FROM genres ORDER BY genre_id ASC";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        try {
            String sqlQuery = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Rating> getAllRatings() {
        String sqlQuery = "SELECT * FROM ratings";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Optional<Rating> getRating(int ratingId) {
        try {
            String sqlQuery = "SELECT rating_id, rating_name FROM ratings WHERE rating_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, ratingId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

//    @Override
//    public List<Film> searchFilmsByNameAndDirectors(String query, List<String> by) {
//        return null;
//    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film result = new Film();
        result.setId(resultSet.getInt("film_id"));
        result.setName(resultSet.getString("film_name"));
        result.setDescription(resultSet.getString("film_description"));
        result.setReleaseDate(resultSet.getDate("film_release_date").toLocalDate());
        result.setDuration(resultSet.getInt("film_duration"));
        Rating rating = new Rating();
        rating.setId(resultSet.getInt("film_rating_id"));
        result.setMpa(rating);
        return result;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre result = new Genre();
        result.setId(resultSet.getInt("genre_id"));
        result.setName(resultSet.getString("genre_name"));
        return result;
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        Rating result = new Rating();
        result.setId(resultSet.getInt("rating_id"));
        result.setName(resultSet.getString("rating_name"));
        return result;
    }

    private Film getFilmById(int id) {
        return getFilm(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find film"));
    }
//<<<<<<< HEAD
//=======

    @Override
    public List<Film> searchFilmsByNameAndDirectors(String query, List<String> searchBy) {
        List<Film> result = new ArrayList<>();
        String queryModified = "%" + query.toLowerCase() + "%";

        if (searchBy.size() == 1) {
            if (searchBy.get(0).equals("title")) {
                String sqlSearchByName = "SELECT * FROM films AS f " +
                        "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
                        "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                        "WHERE LOWER(f.film_name) LIKE ?";
                result = jdbcTemplate.query(sqlSearchByName, new MapRowToFilm(), queryModified);
            }

            if (searchBy.get(0).equals("director")) {
                String sqlSearchByDir = "SELECT * FROM films AS f " +
                        "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
                        "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                        "WHERE LOWER(d.director_name) LIKE ?";
                result = jdbcTemplate.query(sqlSearchByDir, new MapRowToFilm(), queryModified);
            }
        }

        if (searchBy.size() > 1) {
            String sqlSearchByDir = "SELECT * FROM films AS f " +
                    "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE LOWER(d.director_name) LIKE ?";

            result = jdbcTemplate.query(sqlSearchByDir, new MapRowToFilm(), queryModified);

            String sqlSearchByName = "SELECT * FROM films AS f " +
                    "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE LOWER(f.film_name) LIKE ?";
            result.addAll(jdbcTemplate.query(sqlSearchByName, new MapRowToFilm(), queryModified));
        }

        for (Film y : result) {
            String sqlQueryGenres = "SELECT g.genre_id, g.genre_name FROM film_genres AS fg " +
                    "JOIN genres AS g ON g.genre_id = fg.genre_id WHERE film_id = ?";
            List<Genre> genres = jdbcTemplate.query(sqlQueryGenres, new MapRowToGenre(), y.getId());
            if (genres.size() > 0) {
                y.setGenres(new HashSet<>(genres));
            }
        }

        for (Film z : result) {
            String sqlQueryRating = "SELECT rating_id, rating_name FROM ratings WHERE rating_id = ?";
            Optional<Rating> rating = Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlQueryRating, this::mapRowToRating, z.getMpa().getId())
            );
            rating.ifPresent(z::setMpa);
        }
        return result;
    }

//>>>>>>> parent of 328d6b8 (fix: resolve issues)
}
