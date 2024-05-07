package com.example.social_network.services;

import com.example.social_network.entity.PageVisit;
import com.example.social_network.repositories.PageVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PageVisitService {
    @Autowired
    private PageVisitRepository pageVisitRepository;

    public void recordVisit(Long userId, Long visitorId) {
        PageVisit visit = new PageVisit();
        visit.setUserPageId(userId);
        visit.setVisitorId(visitorId);
        pageVisitRepository.save(visit);
    }

    public int getVisitByProfileAndDateRange(Long userId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return pageVisitRepository.findVisitByUserIdAndDateRange(userId, startDateTime, endDateTime).size();
    }
}
