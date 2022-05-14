package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @GetMapping("/films")
    public ResponseEntity<Iterable<Film>> getAllPosts() {
        return ResponseEntity.ok(films);
    }

    @PostMapping("/film")
    public ResponseEntity<?> create(HttpServletRequest request, @Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        films.add(film);
        return ResponseEntity.ok(film);
    }

    @PatchMapping("/film/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody Film film, @PathVariable int id, Errors errors) {
        int filmId = findFilmById(id);
        if (filmId == -1) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        films.set(filmId, film);
        return ResponseEntity.ok(film);
    }

    int findFilmById(int id) {
        Optional<Film> result = films.stream().filter( i -> i.getId() == id).findAny();
        return result.isEmpty() ? -1 : films.indexOf(result.get());
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
