package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

@Data
public class User {
    private int id;
    @Email(message = "Email should be in right format")
    @NotBlank(message = "Email can't be blank")
    @NotNull(message = "Email can't be null")
    private String email;
    private String login;
    private String name;
    @Past(message = "Birthday should be in the past")
    private LocalDate birthday;
}
