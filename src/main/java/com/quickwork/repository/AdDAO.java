package com.quickwork.repository;

import com.quickwork.model.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdDAO extends JpaRepository<Ad, Long> {


}
