package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.DbEventStorage;

import java.util.List;

@Service
public class EventService {
    DbEventStorage dbEventStorage;

    @Autowired
    public EventService(final DbEventStorage dbEventStorage) {
        this.dbEventStorage = dbEventStorage;
    }

    public void addLike(int filmId, int userId) {
        dbEventStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        dbEventStorage.removeLike(filmId,userId);
    }

    public void addFriend(int userId, int friendId) {
       dbEventStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        dbEventStorage.removeFriend(userId, friendId);
    }

    public void addReview(int filmId, int userId) {
        dbEventStorage.addReview(filmId, userId);
    }

    public void removeReview(int filmId, int userId) {
        dbEventStorage.removeReview(filmId, userId);
    }

    public void updateReview(int filmId, int userId) {
        dbEventStorage.updateReview(filmId, userId);
    }

    public List<Event> getFeed() {
        return dbEventStorage.getFeed();
    }
}
