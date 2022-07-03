package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class DbEventStorage implements EventStorage {
    JdbcTemplate jdbcTemplate;
    private static final String sqlQuery = "INSERT INTO " +
            "EVENTS(event_time, user_id, event_type_id, event_operation_id, entity_id ) " +
            "VALUES(?,?,?,?,?)";

    @Autowired
    public DbEventStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 1, 2, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 1, 1, filmId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 3, 2, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 3, 1, friendId);
    }

    @Override
    public void addReview(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 2, 2, filmId);
    }

    @Override
    public void removeReview(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 2, 1, filmId);
    }

    @Override
    public void updateReview(int filmId, int userId) {
        jdbcTemplate.update(sqlQuery, new Timestamp(System.currentTimeMillis()), userId, 2, 3, filmId);
    }

    @Override
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

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("event_id"));
        event.setUserId(resultSet.getInt("user_id"));
        event.setEntityId(resultSet.getInt("entity_id"));
        event.setEventType(resultSet.getString("event_type_name"));
        event.setOperation(resultSet.getString("event_operation_name"));
        event.setTimestamp(resultSet.getTimestamp("event_time"));
        return event;
    }
}