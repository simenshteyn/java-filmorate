package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    void addReview(int filmId, int userId);

    void removeReview(int filmId, int userId);

    void updateReview(int filmId, int userId);

    List<Event> getFeed();
}
