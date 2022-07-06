package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Rating {
    private int id;
    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    private String name;
}
