package com.eventplanner.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    //Request datası taşıyan yer
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Event ID is required")
    private String eventId;
    
    @NotNull(message = "Number of tickets is required")
    @Min(value = 1, message = "At least one ticket must be booked")
    private Integer numberOfTickets;
}
