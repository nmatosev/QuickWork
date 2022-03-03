package com.quickwork.repository;

import com.quickwork.model.ProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePicDAO extends JpaRepository<ProfilePic, Long> {
}
