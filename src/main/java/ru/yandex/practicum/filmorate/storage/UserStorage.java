package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    /**
     * Add User object to storage.
     * @param user User object to add to the storage.
     * @return Added User object.
     */
    User addUser(User user);

    /**
     * Remove User object from storage by ID.
     * @param userId ID of User to remove.
     * @return Added User object or null if has no success.
     */
    User removeUser(int userId);

    /**
     * Update User object in storage by ID with new User object.
     * @param userId ID of user to update.
     * @param user User object to update with.
     * @return Updated User object.
     */
    User updateUser(int userId, User user);

    /**
     * Get User from storage by ID.
     * @param userId ID of User to get from storage.
     * @return User object found in storage with given ID or null if not found.
     */
    User getUser(int userId);

    /**
     * Get User objects from storage with amount and offset parameters.
     * @param amount Amount of users to get from storage.
     * @param offset Offset of users to search from storage.
     * @return List of User objects or null if not found.
     */
    List<User> getUsers(int amount, int offset);

    /**
     * Get list of all users from storage.
     * @return List of User objects.
     */
    List<User> getAllUsers();
}
