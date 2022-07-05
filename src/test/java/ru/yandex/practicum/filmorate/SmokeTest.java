package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class SmokeTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(filmController).isNotNull();
        assertThat(userController).isNotNull();
    }
}
