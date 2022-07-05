package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Reviews;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MapRowToReview implements RowMapper<Reviews> {
    @Override
    public Reviews mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Reviews(
                rs.getInt("review_id"),
                Timestamp.valueOf(LocalDateTime.now()),
                rs.getInt("film_id"),
                rs.getInt("user_id"),
                rs.getString("review_text"),
                rs.getBoolean("is_positive"),
                rs.getInt("feedback"));
    }
}
