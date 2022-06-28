package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class DbEventStorage implements EventStorage{
    @Override
    public void registerEvent() {

    }

    @Override
    public List<Event> getFeed() {
        return null;
    }
}
