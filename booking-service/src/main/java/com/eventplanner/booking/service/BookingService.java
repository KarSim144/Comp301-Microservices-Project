package com.eventplanner.booking.service;

import com.eventplanner.booking.dto.*;
import com.eventplanner.booking.exception.InsufficientSeatsException;
import com.eventplanner.booking.exception.ResourceNotFoundException;
import com.eventplanner.booking.exception.ServiceUnavailableException;
import com.eventplanner.booking.model.Booking;
import com.eventplanner.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final WebClient eventServiceClient;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1. event servis cagir
        log.info("Fetching event details for eventId: {}", request.getEventId());
        EventResponse event = getEventFromService(request.getEventId());

        // 2. kontrol et
        if (!"PUBLISHED".equals(event.getStatus())) {
            throw new IllegalStateException("Event is not available for booking. Status: " + event.getStatus());
        }

        // 3. yer varmi bak
        if (event.getAvailableSeats() < request.getNumberOfTickets()) {
            throw new InsufficientSeatsException(
                    String.format("Insufficient seats. Requested: %d, Available: %d",
                            request.getNumberOfTickets(), event.getAvailableSeats())
            );
        }

        // 4. fiyat belirleme
        Double totalAmount = event.getPrice() * request.getNumberOfTickets();

        // 5. booking olusturma
        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .numberOfTickets(request.getNumberOfTickets())
                .totalAmount(totalAmount)
                .status(Booking.BookingStatus.PENDING)
                .build();
        booking = bookingRepository.save(booking);
        log.info("Booking created with id: {}", booking.getId());

        // 6. event servicede koltuk sayisi guncelleme
        updateEventSeats(request.getEventId(), request.getNumberOfTickets());

        return convertToResponse(booking);
    }

    //create booking metodu için GET http://localhost:8082/api/events/e cagri
  private EventResponse getEventFromService(String eventId) {
    try {
        return eventServiceClient.get()
                .uri("/api/events/" + eventId)
                .retrieve()
                .bodyToMono(EventResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    } catch (WebClientResponseException.NotFound e) {
        log.error("Event not found: {}", eventId);
        throw new ResourceNotFoundException("Event not found with id: " + eventId);
    } catch (WebClientResponseException e) {
        log.error("Error calling Event Service: {}", e.getMessage());
        throw new ServiceUnavailableException("Event Service is currently unavailable");
    } catch (Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        throw new ServiceUnavailableException("Unable to validate event details");
    }
}

private void updateEventSeats(String eventId, Integer seatsToBook) {
    try {
        UpdateSeatsRequest updateRequest = new UpdateSeatsRequest(seatsToBook);

        eventServiceClient.patch()
                .uri("/api/events/" + eventId + "/seats")
                .body(Mono.just(updateRequest), UpdateSeatsRequest.class)
                .retrieve()
                .bodyToMono(EventResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        log.info("Updated seats for event: {}, booked: {}", eventId, seatsToBook);
    } catch (Exception e) {
        log.error("Failed to update event seats: {}", e.getMessage());
    }

}


    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return convertToResponse(booking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse confirmBooking(Long id, String paymentId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);
        booking = bookingRepository.save(booking);
        log.info("Booking confirmed: {}, paymentId: {}", id, paymentId);
        return convertToResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled: {}", id);
    }

    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .eventId(booking.getEventId())
                .numberOfTickets(booking.getNumberOfTickets())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .paymentId(booking.getPaymentId())
                .bookingDate(booking.getBookingDate())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
