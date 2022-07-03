package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapRowToFilm implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film result = new Film();
        result.setId(resultSet.getInt("film_id"));
        result.setName(resultSet.getString("film_name"));
        result.setDescription(resultSet.getString("film_description"));
        result.setReleaseDate(resultSet.getDate("film_release_date").toLocalDate());
        result.setDuration(resultSet.getInt("film_duration"));
        Rating rating = new Rating();
        rating.setId(resultSet.getInt("film_rating_id"));
        result.setMpa(rating);

        Director directors = new Director();
        directors.setId(resultSet.getInt("director_id"));
        directors.setName(resultSet.getString("director_name"));
        if (directors.getId() == 0) {
            result.setDirectors(new ArrayList<>());
        } else {
            result.getDirectors().add(directors);
        }

        return result;
    }

}
