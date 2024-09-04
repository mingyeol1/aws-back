package com.project.react_tft.Repository;

import com.project.react_tft.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.movie.movie_id = :movie_id")
    Page<Review> listOfReview(@Param("movie_id") Long movie_id, Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.review_star), 0) FROM Review r WHERE r.movie.movie_id = :movie_id")
    int getSumStarRatingByMovieId(@Param("movie_id") Long movie_id);

    /* @Query("SELECT r FROM Review r WHERE r.movie.movie_id = :movieId")
    Page<Review> findReviewsByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.member WHERE r.movie.movie_id = :movieId")
    Page<Review> findReviewsWithMemberByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.member.mid = :userId")
    Page<Review> findReviewsByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT AVG(r.review_star) FROM Review r WHERE r.movie.movie_id = :movieId")
    Double getAverageStarRatingByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.movie.movie_id = :movieId")
    Long getReviewCountByMovieId(@Param("movieId") Long movieId);*/



}
