package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> makeFriends(@PathVariable int id, @PathVariable int friendId) {
        Optional<List<User>> friends = Optional.ofNullable(userService.makeFriends(id, friendId));
        if (friends.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find user");
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriends(@PathVariable int id, @PathVariable int friendId) {
        Optional<List<User>> friends = Optional.ofNullable(userService.removeFriends(id, friendId));
        if (friends.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find user");
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<?> getUserFriends(@PathVariable int id) {
        Optional<List<User>> friends = Optional.ofNullable(userStorage.getUserFriends(id));
        if (friends.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find user");
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity<?> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        Optional<Set<Integer>> friendsIds = Optional.ofNullable(userService.showCommonFriends(id, otherId));
        if (friendsIds.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find user");
        List<User> result = new ArrayList<>();
        friendsIds.get().forEach(i -> result.add(userStorage.getUser(i)));
        return ResponseEntity.ok(result);
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
        userStorage.updateUser(id, user);
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
