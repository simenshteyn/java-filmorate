package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToEvent;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class DbEventStorage {
    JdbcTemplate jdbcTemplate;
    private static final String sqlQuery = "INSERT INTO " +
            "EVENTS(event_time, user_id, event_type_id, event_operation_id, entity_id ) " +
            "VALUES(?,?,?,?,?)";

    @Autowired
    public DbEventStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 1, 2, filmId);
    }

    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 1, 1, filmId);
    }

    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 3, 2, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 3, 1, friendId);
    }

    public void addReview(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 2, 2, filmId);
    }

    public void removeReview(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 2, 1, filmId);
    }

    public void updateReview(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 2, 3, filmId);
    }


    public List<Event> getFeed(int id) {
        String getFeedQuery =
                "SELECT" +
                        " e.event_id," +
                        " e.user_id," +
                        " e.entity_id," +
                        " et.event_type_name," +
                        " eo.event_operation_name," +
                        " e.event_time" +
                        " FROM events e " +
                        "JOIN event_operations eo ON e.event_operation_id = eo.event_operation_id" +
                        " JOIN event_Types et ON e.event_type_id = et.event_type_id" +
                        " WHERE e.user_id = ?";
        return jdbcTemplate.query(getFeedQuery, new MapRowToEvent(), id);
    }
}