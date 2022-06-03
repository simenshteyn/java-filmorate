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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping("/films")
    public List<Film> getAllPosts() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable int id) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilm(id));
        if (film.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        return ResponseEntity.ok(film.get());

    }

    @PutMapping("/films")
    public ResponseEntity<?> updateFilm(HttpServletRequest request, @Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        Optional<Film> filmSearch = Optional.ofNullable(filmStorage.getFilm(film.getId()));
        if (filmSearch.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        filmStorage.updateFilm(film.getId(), film);
        return ResponseEntity.ok(film);
    }


    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        Optional<Film> film = Optional.ofNullable(filmService.addLike(userId, id));
        if (film.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        return ResponseEntity.ok(film.get());
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
        Optional<Film> film = Optional.ofNullable(filmService.removeLike(userId, id));
        if (film.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        return ResponseEntity.ok(film.get());
    }

    @GetMapping("/films/popular")
    public ResponseEntity<?> getTopFilms(@RequestParam Optional<Integer> count) {
        return ResponseEntity.ok(filmService.showTopFilms(count.orElse(10)));

    }

    @PostMapping("/films")
    public ResponseEntity<?> create(HttpServletRequest request, @Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        filmStorage.addFilm(film);
        return ResponseEntity.ok(film);
    }

    @PatchMapping("/film/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody Film film, @PathVariable int id, Errors errors) {
        Optional<Film> filmSearch = Optional.ofNullable(filmStorage.getFilm(id));
        if (filmSearch.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Unable to find");
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        filmStorage.updateFilm(id, film);
        return ResponseEntity.ok(film);
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
