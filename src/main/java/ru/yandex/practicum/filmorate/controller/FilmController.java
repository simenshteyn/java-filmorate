package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getAllPosts() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PutMapping("/films")
    public ResponseEntity<?> updateFilm(HttpServletRequest request, @Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        return ResponseEntity.ok(filmService.updateFilm(film.getId(), film));
    }


    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        return ResponseEntity.ok(filmService.addLike(userId, id));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
        return ResponseEntity.ok(filmService.removeLike(userId, id));
    }

    @Validated
    @GetMapping("/films/popular")
    public ResponseEntity<?> getTopFilms(@Positive @RequestParam(required = false, defaultValue = "10") int count) {
        return ResponseEntity.ok(filmService.showTopFilms(count));
    }

    @PostMapping("/films")
    public ResponseEntity<?> create(HttpServletRequest request, @Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        return ResponseEntity.ok(filmService.addFilm(film));
    }

    @PatchMapping("/film/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody Film film, @PathVariable int id, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        return ResponseEntity.ok(filmService.updateFilm(id, film));
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
