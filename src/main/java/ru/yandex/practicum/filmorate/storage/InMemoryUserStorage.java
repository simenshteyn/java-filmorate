package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final List<User> storage = new ArrayList<>();
    private int idCounter = 0;
    @Override
    public User addUser(User user) {
        user.setId(++idCounter);
        if (user.getName().isBlank()) user.setName(user.getLogin());
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
    public Optional<User> updateUser(int userId, User user) {
        int result = findUserIndexById(userId);
        if (result != -1) {
            storage.set(result, user);
            return Optional.of(storage.get(result));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUser(int userId) {
        int result = findUserIndexById(userId);
        if (result != -1) return Optional.of(storage.get(result));
        return Optional.empty();
    }

    @Override
    public Optional<List<User>> getUserFriends(int userId) {
        int result = findUserIndexById(userId);
        List<User> friendsList = new ArrayList<>();
        if (result != -1) {
            Set<Integer> friendsIds = storage.get(result).getFriends();
            friendsIds.forEach(i -> friendsList.add(getUser(i).get()));
            return Optional.of(friendsList);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getUsers(int limit, int offset) {
        return storage.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<User> getAllUsers() {
        return storage;
    }

    @Override
    public List<User> saveFriendship(int firstUserId, int secondUserId) {
        User first = getUserById(firstUserId);
        User second = getUserById(secondUserId);
        first.getFriends().add(secondUserId);
        second.getFriends().add(firstUserId);
        return List.of(first, second);
    }

    @Override
    public List<User> removeFriendship(int firstUserId, int secondUserId) {
        User first = getUserById(firstUserId);
        User second = getUserById(secondUserId);
        first.getFriends().remove(secondUserId);
        second.getFriends().remove(firstUserId);
        return List.of(first, second);
    }

    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        User first = getUserById(firstUserId);
        User second = getUserById(secondUserId);
        List<User> result = new ArrayList<>();
        first.getFriends().stream()
                .filter(second.getFriends()::contains)
                .collect(Collectors.toSet())
                .forEach(i -> result.add(getUserById(i)));
        return result;
    }

    private int findUserIndexById(int id) {
        Optional<User> result = storage.stream().filter(i -> i.getId() == id).findAny();
        return result.isEmpty() ? -1 : storage.indexOf(result.get());
    }

    private User getUserById(int id) {
        return getUser(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
    }
}
