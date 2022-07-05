package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Genre {
    private int id;
    private String name;
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("genre_id", id);
        values.put("genre_name", name);
        return values;
    }
}
