package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("dbUserStorage")
public class DbUserStorage implements UserStorage {
    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User removeUser(int userId) {
        return null;
    }

    @Override
    public void removeAll() {

    }

    @Override
    public Optional<User> updateUser(int userId, User user) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUser(int userId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<User>> getUserFriends(int userId) {
        return Optional.empty();
    }

    @Override
    public List<User> getUsers(int limit, int offset) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
