package com.example.social_network.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "page_visits")
public class PageVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int visitId;

    @Column(name = "user_page_id", nullable = false)
    private Long userPageId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "visit_timestamp", nullable = false)
    private LocalDateTime visitTimestamp  = LocalDateTime.now();

    @Column(name = "visitor_id")
    private Long visitorId;
}