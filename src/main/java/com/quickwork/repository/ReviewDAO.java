package com.quickwork.repository;

import com.quickwork.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewDAO extends JpaRepository<Review, Long> {
}
