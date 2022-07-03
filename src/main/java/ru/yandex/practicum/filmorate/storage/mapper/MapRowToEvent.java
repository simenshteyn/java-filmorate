package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapRowToEvent implements RowMapper<Event> {
    @Override
    public Event mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("event_id"));
        event.setUserId(resultSet.getInt("user_id"));
        event.setEntityId(resultSet.getInt("entity_id"));
        event.setEventType(resultSet.getString("event_type_name"));
        event.setOperation(resultSet.getString("event_operation_name"));
        event.setTimestamp(resultSet.getTimestamp("event_time"));
        return event;
    }
}