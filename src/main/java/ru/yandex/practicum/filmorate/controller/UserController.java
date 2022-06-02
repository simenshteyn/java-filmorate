package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return this.userStorage.getAllUsers();
    }

    @PostMapping("/user")
    public ResponseEntity<?> create(HttpServletRequest request, @Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        userStorage.addUser(user);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody User user, @PathVariable int id, Errors errors) {
        Optional<User> userSearch = Optional.ofNullable(userStorage.getUser(id));
        if (userSearch.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        userStorage.updateUser(userSearch.get().getId(), user);
        return ResponseEntity.ok(user);
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
