package com.quickwork.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message", schema = "qw_dta")
@Getter
@Setter
public class Message implements Comparable<Message>{

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
