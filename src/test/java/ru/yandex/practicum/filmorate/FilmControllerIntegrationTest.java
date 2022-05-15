package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private static Film film;

    @BeforeAll
    public static void beforeAll() {
       film = new Film();
       film.setName("film name");
       film.setId(1);
       film.setDescription("some description");
       film.setDuration(180);
       film.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 1));
    }

    @Test
    public void getFilmsEndpoint() throws Exception {
        // Films endpoint should be empty
        mockMvc.perform(get("/films")).andExpect(status().isOk()).andExpect(content().string(containsString("[]")));

        // Add film with POST request
        mockMvc.perform(
                post("/film")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        // Check film has been added
        mockMvc.perform(
                get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(film))));

        // Set new duration
        film.setDuration(90);

        // Patch wrong id should return 404 error
        mockMvc.perform(
                        patch("/film/42")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        // Patch with right id should return new duration of film
        mockMvc.perform(
                patch("/film/" + film.getId())
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk()).andExpect(jsonPath("$.duration").value(90));

        // Check film has been added
        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(film))));
    }
}
