package com.quickwork.repository;

import com.quickwork.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDAO extends JpaRepository<Message, Long> {

    List<Message> findByUser1Id(long user1Id);
    List<Message> findByUser2Id(long user1Id);

}
