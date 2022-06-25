package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    /**
     * Make friendship between two Users by ID.
     * @param firstUserId ID of first user.
     * @param secondUserId ID of second user
     * @return List of Users if addition was successfull.
     */
    public List<User> makeFriends(int firstUserId, int secondUserId) {
        return storage.saveFriendship(firstUserId, secondUserId);
    }

    /**
     * Remove friendship between two Users by ID
     * @param firstUserId ID of first user.
     * @param secondUserId ID of second user.
     * @return List of Users with removed friendship.
     */
    public List<User> removeFriends(int firstUserId, int secondUserId) {
        return storage.removeFriendship(firstUserId, secondUserId);
    }

    /**
     * Show common friends IDs between two users.
     * @param firstUserId ID of first user.
     * @param secondUserId ID of second user.
     * @return Set of common friends IDs.
     */
    public List<User> showCommonFriends(int firstUserId, int secondUserId) {
        return storage.getCommonFriends(firstUserId, secondUserId);
    }

    public List<User> getAllUsers() { return storage.getAllUsers(); }

    public User getUserById(int id) {
        return storage.getUser(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
    }

    public List<User> getUserFriends(int id) {
        return storage.getUserFriends(id).orElseThrow(()-> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
    }

    public User addUser(User user) { return storage.addUser(user); }

    public User updateUser(int id, User user) {
        return storage.updateUser(id, user).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unable to find user"));
    }
}
