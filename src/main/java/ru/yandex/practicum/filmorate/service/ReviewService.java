package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.DbEventStorage;
import ru.yandex.practicum.filmorate.storage.DbReviewStorage;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ReviewService {
    private DbReviewStorage reviewStorage;
    private UserService userService;
    private FilmService filmService;

    @Autowired
    public ReviewService(DbReviewStorage reviewStorage,
                         UserService userService,
                         FilmService filmService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;

    }

    public Reviews addReview(Reviews review) {
        if ((userService.getUserById(review.getUserId()) == null) || (filmService.getFilmById(review.getFilmId()) == null)) {
            throw new ResponseStatusException(BAD_REQUEST, "Unable to find film's or user's id");
        } else {
            return reviewStorage.addReview(review);
        }
    }

    public Reviews updateReview(Reviews review) {
        return reviewStorage.updateReview(review);
    }

    public Reviews deleteReviewById(Integer id) {
        return reviewStorage.deleteReviewById(id);
    }

    public Reviews getReviewById(Integer id) {
        return reviewStorage.getReviewById(id);
    }

    public List<Reviews> getReviewsOfFilm(Integer filmId, int count) {
        return reviewStorage.getReviewsOfFilm(filmId, count);
    }

    public void addLikeOrDislikeReview(Integer id, Integer userId, Boolean mark) {
        reviewStorage.addLikeOrDislikeReview(id, userId, mark);
    }

    public void deleteLikeOrDislikeReview(Integer id, Integer userId) {
        reviewStorage.deleteLikeOrDislikeReview(id, userId);
    }
}