package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.EventStorage;
@Service
public class EventService {
    EventStorage eventStorage;
@Autowired
    public EventService(final EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public void registerEvent(){}
}
