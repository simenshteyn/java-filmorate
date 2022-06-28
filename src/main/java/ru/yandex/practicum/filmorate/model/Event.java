package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Event {
    int eventId;
    int userId;
    String eventType;
    String operation;
    int entityId;
    Timestamp timestamp;
}
