package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Qualifier("dbUserStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());

        String sqlQuery = "INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        int userId = keyHolder.getKey().intValue();
        String sqlQuerySearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToUser, userId);
    }

    @Override
    public User removeUser(int userId) {
        String sqlQuerySearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
        Optional<User> result = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToUser, userId));
        if (result.isEmpty()) return null;
        String sqlQuery = "DELETE FROM users where user_id = ?";
        jdbcTemplate.update(sqlQuery, userId);
        return result.get();
    }

    @Override
    public void removeAll() {
        String sqlQuery = "DELETE FROM users";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Optional<User> updateUser(int userId, User user) {
        try {
            String sqlQuerySearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
            User result = jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        String sqlQuery = "UPDATE users SET user_email = ?, user_login = ?, user_name = ?, user_birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);
        String sqlQuerySecondSearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuerySecondSearch, this::mapRowToUser, userId));
    }

    @Override
    public Optional<User> getUser(int userId) {
        try {
            String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<User>> getUserFriends(int userId) {
        try {
            String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id IN" +
                "(SELECT to_id FROM friendships WHERE from_id = ? AND is_approved = true UNION " +
                "SELECT from_id FROM friendships WHERE to_id = ? AND is_approved = true)";
            return Optional.of(jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getUsers(int limit, int offset) {
        String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, limit, offset);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public List<User> saveFriendship(int firstUserId, int secondUserId) {
        User first = getUserById(firstUserId);
        User second = getUserById(secondUserId);
        String sqlQuery = "INSERT INTO friendships VALUES (?, ?, true)";
        jdbcTemplate.update(sqlQuery, firstUserId, secondUserId);
        jdbcTemplate.update(sqlQuery, secondUserId, firstUserId);
        return List.of(first, second);
    }

    @Override
    public List<User> removeFriendship(int firstUserId, int secondUserId) {
        User first = getUserById(firstUserId);
        User second = getUserById(secondUserId);
        String sqlQuery = "DELETE FROM friendships WHERE (from_id, to_id) IN ((?, ?)) AND is_approved = true";
        jdbcTemplate.update(sqlQuery, firstUserId, secondUserId);
        jdbcTemplate.update(sqlQuery, secondUserId, firstUserId);
        return List.of(first, second);
    }

    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        User first = getUserById(firstUserId);
        User second = getUserById(secondUserId);
        List<User> result = new ArrayList<>();
        try {
            String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday " +
                    "FROM users WHERE user_id IN (SELECT DISTINCT from_id FROM friendships WHERE to_id = ? AND is_approved = true " +
                    "INTERSECT SELECT DISTINCT from_id FROM friendships WHERE to_id = ? AND is_approved = true)";
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, firstUserId, secondUserId);
        } catch (EmptyResultDataAccessException e) {
            return result;
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User result = new User();
        result.setId(resultSet.getInt("user_id"));
        result.setEmail(resultSet.getString("user_email"));
        result.setLogin(resultSet.getString("user_login"));
        result.setName(resultSet.getString("user_name"));
        result.setBirthday(resultSet.getDate("user_birthday").toLocalDate());
        return result;
    }

    private User getUserById(int id) {
        return getUser(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
    }
}
