package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryUserStorage implements UserStorage {
    private List<User> storage = new ArrayList<>();
    @Override
    public User addUser(User user) {
       storage.add(user);
       int userIndex = storage.indexOf(user);
       return storage.get(userIndex);
    }

    @Override
    public User removeUser(int userId) {
        int result = findUserIndexById(userId);
        if (result != -1) return storage.remove(result);
        return null;
    }

    @Override
    public void removeAll() {
        storage.clear();
    }

    @Override
    public User updateUser(int userId, User user) {
        int result = findUserIndexById(userId);
        if (result != -1) {
            storage.set(result, user);
            return storage.get(result);
        }
        return null;
    }

    @Override
    public User getUser(int userId) {
        int result = findUserIndexById(userId);
        if (result != -1) return storage.get(result);
        return null;
    }

    @Override
    public List<User> getUsers(int limit, int offset) {
        return storage.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<User> getAllUsers() {
        return storage;
    }

    private int findUserIndexById(int id) {
        Optional<User> result = storage.stream().filter(i -> i.getId() == id).findAny();
        return result.isEmpty() ? -1 : storage.indexOf(result.get());
    }
}
