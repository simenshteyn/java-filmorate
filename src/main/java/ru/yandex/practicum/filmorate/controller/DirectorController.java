package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getSortedFilmsByDirectors(@PathVariable int directorId,
                                                @RequestParam(required = false) String sortBy) {
        return directorService.getSortedFilmsByDirectors(directorId, sortBy);
    }

    @GetMapping("/directors")
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director addDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        directorService.deleteDirectorById(id);
    }
}
