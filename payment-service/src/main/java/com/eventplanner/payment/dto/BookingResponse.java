package com.eventplanner.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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