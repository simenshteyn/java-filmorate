package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.validator.FilmorateValidationErrorBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Slf4j
@Validated
public class ReviewsController {
    private ReviewService reviewService;

    @Autowired
    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(HttpServletRequest request, @RequestBody @Valid Reviews reviews, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        return ResponseEntity.ok(reviewService.addReview(reviews));
    }

    @PutMapping("/reviews")
    public ResponseEntity<?> updateReview(HttpServletRequest request, @RequestBody @Valid Reviews reviews, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Validation error with request: " + request.getRequestURI());
            return ResponseEntity.badRequest()
                    .body(FilmorateValidationErrorBuilder.fromBindingErrors(errors));
        }
        return ResponseEntity.ok(reviewService.updateReview(reviews));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReviewById(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.deleteReviewById(id));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getReviewsOfFilm(@RequestParam(required = false, defaultValue = "0") Integer filmId,
                                              @Positive @RequestParam(required = false, defaultValue = "10") int count) {
        return ResponseEntity.ok(reviewService.getReviewsOfFilm(filmId, count));
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLikeReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addLikeOrDislikeReview(id, userId, true);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public ResponseEntity<?> addDislikeReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addLikeOrDislikeReview(id, userId, false);
        return null;
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public ResponseEntity<?> deleteLikeReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.deleteLikeOrDislikeReview(id, userId);
        return null;
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public ResponseEntity<?> deleteDislikeReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.deleteLikeOrDislikeReview(id, userId);
        return null;
    }
}
