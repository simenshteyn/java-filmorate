package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Director {

    private int id;
    private String name;
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", id);
        values.put("director_name", name);
        return values;
    }
}
