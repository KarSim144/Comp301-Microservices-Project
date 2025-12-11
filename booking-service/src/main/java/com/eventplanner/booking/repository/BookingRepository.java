package com.eventplanner.booking.repository;

import com.eventplanner.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
//id ile bulma işlemleri için
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByEventId(String eventId);
    List<Booking> findByStatus(Booking.BookingStatus status);
}
