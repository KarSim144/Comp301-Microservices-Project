package com.eventplanner.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//data transfer objesi
public class BookingResponse {
    private Long id;
    private Long userId;
    private String eventId;
    private Integer numberOfTickets;
    private Double totalAmount;
    private String status;
    private String paymentId;
    private LocalDateTime bookingDate;
    private LocalDateTime updatedAt;
}
