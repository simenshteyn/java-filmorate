package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Reviews {
    private int id;
    private Timestamp timeStamp;
    @NotNull(message = "Film id can't be null")
    private Integer filmId;
    @NotNull(message = "User id can't be null")
    private Integer userId;
    @NotBlank(message = "Review can't be blank")
    @NotNull(message = "Review can't be null")
    @JsonProperty("content")
    private String reviewText;
    @NotNull(message = "Type of review can't be null")
    private Boolean isPositive;
    private Integer useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("review_timestamp", Timestamp.valueOf(LocalDateTime.now()));
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("review_text", reviewText);
        values.put("is_positive", isPositive);
        return values;
    }
}
