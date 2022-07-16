package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowToReview;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class DbReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private DbEventStorage eventStorage;

    public DbReviewStorage(JdbcTemplate jdbcTemplate, DbEventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventStorage = eventStorage;
    }

    public Reviews addReview(Reviews review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        int id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
        Reviews reviewToReturn = getReviewById(id);
        eventStorage.addReview(id, reviewToReturn.getUserId());
        return reviewToReturn;
    }

    public Reviews updateReview(Reviews review) {
        if (!checkContainReview(review.getId())) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find review's id");
        }
        String sql = "UPDATE reviews SET review_text = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getReviewText(), review.getIsPositive(), review.getId());
        Reviews reviewToReturn = getReviewById(review.getId());
        eventStorage.updateReview(reviewToReturn.getFilmId(), reviewToReturn.getUserId());
        return reviewToReturn;
    }

    public Reviews getReviewById(Integer id) {
        if (!checkContainReview(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find review's id");
        }
        String sql = "select r.review_id, " +
                "r.review_timestamp, " +
                "r.user_id, " +
                "r.film_id, " +
                "r.review_text, " +
                "r.is_positive, " +
                "sum(f.feedback_mark) as feedback " +
                "from reviews as r " +
                "left join reviews_feedback as f " +
                "on r.review_id = f.review_id " +
                "where r.review_id = ? " +
                "group by r.review_id";
        return jdbcTemplate.queryForObject(sql, new MapRowToReview(), id);
    }

    public Reviews deleteReviewById(Integer id) {
        Reviews reviews = getReviewById(id);
        String sqlReview = "delete from reviews where review_id = ?";
        jdbcTemplate.update(sqlReview, id);
        String sqlFeedback = "delete from reviews_feedback where review_id = ?";
        jdbcTemplate.update(sqlFeedback, reviews.getId());
        eventStorage.removeReview(reviews.getFilmId(), reviews.getUserId());
        return reviews;
    }

    public List<Reviews> getReviewsOfFilm(Integer filmId, int count) {
        List<Reviews> reviews = new ArrayList<>();
        if (filmId != 0) {
            String sql = "select r.review_id, " +
                    "r.review_timestamp, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "r.review_text, " +
                    "r.is_positive, " +
                    "COALESCE(sum(f.feedback_mark),0) as feedback " +
                    "from reviews as r " +
                    "left join reviews_feedback as f " +
                    "on r.review_id = f.review_id " +
                    "where r.film_id = ? " +
                    "group by r.review_id " +
                    "order by feedback desc " +
                    "limit ?";
            reviews = jdbcTemplate.query(sql,
                    new MapRowToReview(), filmId, count);
        } else {
            String sql = "select r.review_id, " +
                    "r.review_timestamp, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "r.review_text, " +
                    "r.is_positive, " +
                    "COALESCE(sum(f.feedback_mark),0) as feedback " +
                    "from reviews as r " +
                    "left join reviews_feedback as f " +
                    "on r.review_id = f.review_id " +
                    "group by r.review_id " +
                    "order by feedback desc " +
                    "limit ?";
            reviews = jdbcTemplate.query(sql,
                    new MapRowToReview(), count);
        }
        return reviews;
    }

    public void addLikeOrDislikeReview(Integer id, Integer userId, Boolean mark) {
        String sqlTest = "select review_id from reviews_feedback where user_id = ? and review_id = ?";
        List<Integer> testRequest = jdbcTemplate.query(sqlTest, (rs, rowNum) -> rs.getInt(1), userId, id);
        if (testRequest.size() > 0) {
            throw new ResponseStatusException(NOT_FOUND, "There is already have such feedback.");
        }
        String sql = "insert into reviews_feedback (user_id, review_id, feedback_mark)" +
                "values (?, ?, ?)";
        Integer like = (mark) ? 1 : -1;
        jdbcTemplate.update(sql,
                userId,
                id,
                like);
    }

    public void deleteLikeOrDislikeReview(Integer id, Integer userId) {
        String sqlTest = "select review_id from reviews_feedback where user_id = ? and review_id = ?";
        List<Integer> testRequest = jdbcTemplate.query(sqlTest, (rs, rowNum) -> rs.getInt(1), userId, id);
        if (testRequest.size() == 0) {
            throw new ResponseStatusException(NOT_FOUND, "There no such feedback.");
        }
        String sql = "delete from reviews_feedback where user_id = ? and review_id = ?";
        jdbcTemplate.update(sql, userId, id);
    }

    public Boolean checkContainReview(Integer id) {
        List<Integer> listFind = jdbcTemplate.query("select review_id from reviews where review_id = ?",
                (rs, rowNum) -> rs.getInt(1), id);
        return listFind.size() != 0;
    }
}
