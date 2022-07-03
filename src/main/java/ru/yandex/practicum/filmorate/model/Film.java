package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;

@Data
public class Film {
    private int id;
    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blanc")
    private String name;
    @Size(max=200, message = "Description size can't be more than 200 symbols")
    private String description;
    @ReleaseDate(message = "Release date should be after 28.12.1895")
    private LocalDate releaseDate;
    @Positive(message = "Duration in seconds should be positive number")
    private int duration;
    private Set<Integer> usersLikedIds = new HashSet<>();
    public int countUsersLiked() { return usersLikedIds.size(); }
    private Set<Genre> genres;
    @NotNull(message = "MPA can't be null")
    private Rating mpa;
    private List<Director> directors = new ArrayList<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", id);
        values.put("film_name", name);
        values.put("film_description", description);
        values.put("film_release_date", releaseDate);
        values.put("film_duration", duration);
        if (getMpa() != null) values.put("film_rating_id", mpa.getId());
        if (getDirectors().size() != 0) values.put("director_id", directors.get(0));
        return values;
    }
}

