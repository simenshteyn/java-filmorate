package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class User {
    private int id;
    @Email(message = "Email should be in right format")
    @NotBlank(message = "Email can't be blank")
    @NotNull(message = "Email can't be null")
    private String email;
    @Pattern(regexp = "[A-Za-z\\d.-]{0,19}")
    @NotBlank(message = "Login can't be blank")
    @NotNull(message = "Login can't be null")
    private String login;
    private String name;
    @Past(message = "Birthday should be in the past")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
    private Set<Integer> filmsLiked = new HashSet<>();
}
