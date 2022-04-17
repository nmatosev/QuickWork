package com.quickwork.repository;

import com.quickwork.model.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePicDAO extends JpaRepository<ProfilePicture, Long> {

    Optional<ProfilePicture> findByName(String username);
}
