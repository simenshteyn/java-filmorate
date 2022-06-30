package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@RestController
@Slf4j
@Validated
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

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getGenreById(id));
    }

    @GetMapping("/mpa")
    public List<Rating> getAllRatings() {
        return filmService.getAllRatings();
    }

    @GetMapping("/mpa/{id}")
    public ResponseEntity<?> getRatingById(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getRatingById(id));
    }

    @GetMapping("/films/search")
    public List<Film> searchFilmsByNameAndDirectors(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.searchFilmsByNameAndDirectors(query, by);
    }
}
