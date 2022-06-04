package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    /**
     * Make friendship between two Users by ID.
     * @param firstUserId ID of first user.
     * @param secondUserId ID of second user
     * @return List of Users if addition was successfull or null.
     */
    public List<User> makeFriends(int firstUserId, int secondUserId) {
        Optional<User> first = storage.getUser(firstUserId);
        Optional<User> second = storage.getUser(secondUserId);
        if (first.isPresent() && second.isPresent()) {
            first.get().getFriends().add(secondUserId);
            second.get().getFriends().add(firstUserId);
            return List.of(first.get(), second.get());
        }
        return null;
    }

    /**
     * Remove friendship between two Users by ID
     * @param firstUserId ID of first user.
     * @param secondUserId ID of second user.
     * @return List of Users with removed friendship or null.
     */
    public List<User> removeFriends(int firstUserId, int secondUserId) {
        Optional<User> first = storage.getUser(firstUserId);
        Optional<User> second = storage.getUser(secondUserId);
        if (first.isPresent() && second.isPresent()) {
            first.get().getFriends().remove(secondUserId);
            second.get().getFriends().remove(firstUserId);
            return List.of(first.get(), second.get());
        }
        return null;
    }

    /**
     * Show common friends IDs between two users.
     * @param firstUserId ID of first user.
     * @param secondUserId ID of second user.
     * @return Set of common friends IDs.
     */
    public Set<Integer> showCommonFriends(int firstUserId, int secondUserId) {
        Optional<User> first = storage.getUser(firstUserId);
        Optional<User> second = storage.getUser(secondUserId);
        if (first.isPresent() && second.isPresent()) {
            return first.get().getFriends().stream()
                    .filter(second.get().getFriends()::contains)
                    .collect(Collectors.toSet());
        }
        return null;
    }

    public List<User> getAllUsers() { return storage.getAllUsers(); }

    public User getUserById(int id) {
        return storage.getUser(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
    }

    public List<User> getUserFriends(int id) { return storage.getUserFriends(id); }

    public User addUser(User user) { return storage.addUser(user); }

    public User updateUser(int id, User user) { return storage.updateUser(id, user); }
}
