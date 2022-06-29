package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

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
    public List<Event> getFeed() {
        return null;
    }
}