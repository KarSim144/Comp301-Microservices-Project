package com.eventplanner.eventcatalog.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class UpdateSeatsRequest {
    @NotNull(message = "Seats to book is required")
    @Positive(message = "Seats to book must be positive")
    private Integer seatsToBook;
}