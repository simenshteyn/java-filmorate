package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private static User user;
    private static User friend;

    @BeforeAll
    public static void beforeAll() {
        user = new User();
        user.setId(1);
        user.setName("Testuser");
        user.setLogin("testuser");
        user.setEmail("test@user.com");
        user.setBirthday(LocalDate.of(1970, Month.JANUARY, 1));

        friend = new User();
        friend.setId(42);
        friend.setName("Friendname");
        friend.setLogin("friendlogin");
        friend.setEmail("friend@user.com");
        friend.setBirthday(LocalDate.of(1990, Month.JANUARY, 1));
    }

    @Test
    public void getUsersEndpoint() throws Exception {
        // Films endpoint should be empty
        mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(content().string(containsString("[]")));

        // Add user with POST request
        mockMvc.perform(
                        post("/user")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        // Check film has been added
        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user))));

        // Set new email
        user.setEmail("user@test.com");

        // Patch wrong id should return 404 error
        mockMvc.perform(
                        patch("/user/42")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        // Patch with right id should return new email of user
        mockMvc.perform(
                        patch("/user/" + user.getId())
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("user@test.com"));

        // Check user has been added
        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user))));

        // Add friend user with POST request
        mockMvc.perform(
                        post("/user")
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));

        // Check friend has been added
        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user, friend))));

        // Make friends with wrong ids should return 404 error
        mockMvc.perform(put("/users/777/friends/666")).andExpect(status().isNotFound());

        // Make friends with right ids should work
        mockMvc.perform(put(String.format("/users/%d/friends/%d", user.getId(), friend.getId())))
                .andExpect(status().isOk());

        // Check user friends
        mockMvc.perform(get(String.format("/users/%d/friends", user.getId())))
                .andExpect(status().isOk());

        // Remove friends with wrong ids should return 404 error
        mockMvc.perform(delete("/users/777/friends/666")).andExpect(status().isNotFound());

        // Remove friends with right ids should work
        mockMvc.perform(delete(String.format("/users/%d/friends/%d", user.getId(), friend.getId())))
                .andExpect(status().isOk());

        // Common friends for not existing ids shouldn't be found
        mockMvc.perform(get("/users/777/friends/common/666")).andExpect(status().isNotFound());

        // Empty common list of friends
        mockMvc.perform(get(String.format("/users/%d/friends/common/%d", user.getId(), friend.getId()))).andExpect(status().isOk());
    }
}
