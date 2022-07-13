package com.quickwork.repository;

import com.quickwork.model.County;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountyDAO extends JpaRepository<County, Long> {
}
