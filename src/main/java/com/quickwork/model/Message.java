package com.quickwork.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "message", schema = "qw_dta", indexes = {
        @Index(name = "message_user1_id_idx", columnList = "user1_id"),
        @Index(name = "message_user2_id_idx",  columnList = "user2_id")
})
@Getter
@Setter
public class Message implements Comparable<Message> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    String message;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @CreationTimestamp
    private Date createdDate;

    @Override
    public int compareTo(@NotNull Message o) {
        return this.getCreatedDate().compareTo(o.getCreatedDate());
    }
}
