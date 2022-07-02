package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapRowToFilmDirector implements RowMapper<FilmDirector> {
    @Override
    public FilmDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmDirector filmDirector = new FilmDirector();
        filmDirector.setFilmId(rs.getInt("film_id"));
        filmDirector.setDirectorId(rs.getInt("director_id"));
        return filmDirector;
    }
}
