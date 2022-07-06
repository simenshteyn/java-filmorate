package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Event {
    private int eventId;
    @NotNull (message = "User id should be defined")
    private int userId;
    @NotNull (message = "Event type should be defined")
    private String eventType;
    @NotNull(message = "Operation should be defined")
    private String operation;
    @NotNull(message = "Entity id should be defined")
    private int entityId;
    @NotNull(message = "Date and time should be defined")
    private Long timestamp;
}
