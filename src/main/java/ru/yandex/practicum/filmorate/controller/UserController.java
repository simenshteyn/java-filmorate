package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class UserController {
    private List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return users;
    }

    @PostMapping("/user")
    public ResponseEntity<?> create(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) return ResponseEntity.badRequest()
                .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        users.add(user);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user/{id}")
    public User update(@Valid @RequestBody User user, @PathVariable int id) {
        int userId = findUserById(id);
        if (userId == -1) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        users.set(userId, user);
        return user;
    }

    int findUserById(int id) {
        Optional<User> result = users.stream().filter(i -> i.getId() == id).findAny();
        return result.isEmpty() ? -1 : users.indexOf(result.get());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
