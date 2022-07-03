package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

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
     * @return Optional of User or Empty Optional if ID not found.
     */
    Optional<User> removeUser(int userId);

    /**
     * Remove all users from storage.
     */
    void removeAll();

    /**
     * Update User object in storage by ID with new User object.
     * @param userId ID of user to update.
     * @param user User object to update with.
     * @return Updated User object.
     */
    Optional<User> updateUser(int userId, User user);

    /**
     * Get User from storage by ID.
     * @param userId ID of User to get from storage.
     * @return User object found in storage with given ID or Empty Optional if not found.
     */
    Optional<User> getUser(int userId);

    /**
     * Get User friends by ID.
     * @param userId User ID.
     * @return List of User friends.
     */
    Optional<List<User>> getUserFriends(int userId);

    /**
     * Get User objects from storage with limit and offset parameters.
     * @param limit Amount of users to get from storage.
     * @param offset Offset of users to search from storage.
     * @return List of User objects, could be empty.
     */
    List<User> getUsers(int limit, int offset);

    /**
     * Get list of all users from storage.
     * @return List of User objects.
     */
    List<User> getAllUsers();

    /**
     * Save friendship between two users.
     * @param firstUserId Fist user int id.
     * @param secondUserId Second user int id.
     * @return List of users if saving was successfull.
     */
    List<User> saveFriendship(int firstUserId, int secondUserId);

    /**
     * Remove friendship between two users.
     * @param firstUserId First user int id.
     * @param secondUserId Second user int id.
     * @return List of users if removing was successfull.
     */
    List<User> removeFriendship(int firstUserId, int secondUserId);

    /**
     * Get list of common friends between two users.
     * @param firstUserId First user int id.
     * @param secondUserId Second user int id.
     * @return List of common friends between two users.
     */
    List<User> getCommonFriends(int firstUserId, int secondUserId);
}
