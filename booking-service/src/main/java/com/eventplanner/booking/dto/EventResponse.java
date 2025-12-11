package com.eventplanner.booking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
//Data transfer objesi
public class EventResponse {
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
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}