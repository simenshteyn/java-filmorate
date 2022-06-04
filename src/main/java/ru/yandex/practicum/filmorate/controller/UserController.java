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
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> makeFriends(@PathVariable int id, @PathVariable int friendId) {
        return ResponseEntity.ok(userService.makeFriends(id, friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriends(@PathVariable int id, @PathVariable int friendId) {
        return ResponseEntity.ok(userService.removeFriends(id, friendId));
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<?> getUserFriends(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserFriends(id));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity<?> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return ResponseEntity.ok(userService.showCommonFriends(id, otherId));
    }

    @PostMapping("/users")
    public ResponseEntity<?> create(HttpServletRequest request, @Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        userService.addUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        Optional<User> userSearch = Optional.ofNullable(userService.getUserById(user.getId()));
        if (userSearch.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        userService.updateUser(user.getId(), user);
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }


    @PatchMapping("/user/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody User user, @PathVariable int id, Errors errors) {
        Optional<User> userSearch = Optional.ofNullable(userService.getUserById(id));
        if (userSearch.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        userService.updateUser(id, user);
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
