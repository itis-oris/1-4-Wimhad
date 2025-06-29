package com.skillswap.repository;

import com.skillswap.entity.Review;
import com.skillswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewed(User reviewed);
    List<Review> findByReviewer(User reviewer);
} 