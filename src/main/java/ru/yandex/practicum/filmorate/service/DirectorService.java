package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DirectorService {
    private DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public List<Film> getSortedFilmsByDirectors(int directorId, String sortBy) {
        return directorDao.getSortedFilmsByDirectors(directorId, sortBy);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        return directorDao.getDirectorById(id);
    }

    public Director addDirector(Director director) {
        if(director.getName().isBlank())  {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Incorrect director's name");
        }
        return directorDao.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    public void deleteDirectorById(int id) {
        directorDao.deleteDirectorById(id);
    }
}
