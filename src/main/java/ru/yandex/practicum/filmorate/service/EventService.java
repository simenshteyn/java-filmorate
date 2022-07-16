package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.DbEventStorage;
import ru.yandex.practicum.filmorate.storage.DbReviewStorage;

import java.util.List;

@Service
public class EventService {
    private final DbEventStorage dbEventStorage;

    @Autowired
    public EventService(final DbEventStorage dbEventStorage, DbReviewStorage reviewStorage) {
        this.dbEventStorage = dbEventStorage;
    }

    public List<Event> getFeed(int id) {
        return dbEventStorage.getFeed(id);
    }
}
