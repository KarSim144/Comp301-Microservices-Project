package com.eventplanner.eventcatalog.repository;

import com.eventplanner.eventcatalog.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByCategory(String category);
    List<Event> findByOrganizerId(String organizerId);
    List<Event> findByStatus(Event.EventStatus status);
    List<Event> findByEventDateAfter(LocalDateTime date);
    List<Event> findByTitleContainingIgnoreCase(String title);
}
