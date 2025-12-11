package com.eventplanner.payment.service;

import com.eventplanner.payment.dto.*;
import com.eventplanner.payment.exception.InvalidBookingStateException;
import com.eventplanner.payment.exception.ResourceNotFoundException;
import com.eventplanner.payment.exception.ServiceUnavailableException;
import com.eventplanner.payment.model.Payment;
import com.eventplanner.payment.repository.PaymentRepository;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient bookingServiceClient;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Call Booking Service to validate booking exists and get details
        log.info("Validating booking: {}", request.getBookingId());
        BookingResponse booking = getBookingFromService(request.getBookingId());

        // 2. Validate booking status
        if (!"PENDING".equals(booking.getStatus())) {
            throw new InvalidBookingStateException(
                    "Booking is not in PENDING state. Current status: " + booking.getStatus()
            );
        }

        if (booking.getPaymentId() != null && !booking.getPaymentId().isEmpty()) {
            throw new InvalidBookingStateException(
                    "Booking already has an associated payment: " + booking.getPaymentId()
            );
        }

        if (!booking.getTotalAmount().equals(request.getAmount())) {
            throw new InvalidBookingStateException(
                    String.format("Amount mismatch. Booking: %.2f, Payment: %.2f",
                            booking.getTotalAmount(), request.getAmount())
            );
        }


        if (!booking.getUserId().equals(request.getUserId())) {
            throw new InvalidBookingStateException("User ID does not match booking");
        }

        String transactionId = "TXN-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(Payment.PaymentStatus.COMPLETED)
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment processed: {}, transactionId: {}", payment.getId(), transactionId);


        confirmBookingInService(request.getBookingId(), transactionId);

        return convertToResponse(payment);
    }

    private BookingResponse getBookingFromService(Long bookingId) {
        try {
            BookingResponse booking = bookingServiceClient.get()
                    .uri("/api/bookings/" + bookingId)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            log.info("Booking retrieved: {}", bookingId);
            return booking;
        } catch (WebClientResponseException.NotFound e) {
            log.error("Booking not found: {}", bookingId);
            throw new ResourceNotFoundException("Booking not found with id: " + bookingId);
        } catch (WebClientResponseException e) {
            log.error("Error calling Booking Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Booking Service is currently unavailable");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceUnavailableException("Unable to validate booking details");
        }
    }

    private void confirmBookingInService(Long bookingId, String transactionId) {
        try {
            ConfirmBookingRequest confirmRequest = new ConfirmBookingRequest(transactionId);

            bookingServiceClient.post()
                    .uri("/api/bookings/" + bookingId + "/confirm")
                    .body(Mono.just(confirmRequest), ConfirmBookingRequest.class)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            log.info("Booking confirmed: {}, transactionId: {}", bookingId, transactionId);
        } catch (Exception e) {
            log.error("Failed to confirm booking: {}", e.getMessage());

        }
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return convertToResponse(payment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));
        return convertToResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        log.info("Payment refunded: {}", id);

        return convertToResponse(payment);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}