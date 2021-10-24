package com.quickwork.repository;

import com.quickwork.model.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdDAO extends JpaRepository<Ad, Long> {


    Optional<Ad> findById(long id);


}
