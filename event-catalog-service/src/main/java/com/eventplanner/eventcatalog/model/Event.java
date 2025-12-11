package com.eventplanner.eventcatalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    private String id;
    
    private String title;
    private String description;
    private String category;
    private LocalDateTime eventDate;
    private String location;
    private Integer capacity;
    private Integer availableSeats;
    private Double price;
    private String organizerId;
    private String imageUrl;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum EventStatus {
        DRAFT, PUBLISHED, CANCELLED, COMPLETED
    }
}
