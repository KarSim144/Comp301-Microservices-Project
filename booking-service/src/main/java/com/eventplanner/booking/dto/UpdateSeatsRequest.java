package com.eventplanner.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//dto (manuel işlem içindi)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeatsRequest {
    private Integer seatsToBook;
}