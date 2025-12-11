package com.eventplanner.eventcatalog.service;

import com.eventplanner.eventcatalog.dto.EventRequest;
import com.eventplanner.eventcatalog.dto.EventResponse;
import com.eventplanner.eventcatalog.exception.ResourceNotFoundException;
import com.eventplanner.eventcatalog.model.Event;
import com.eventplanner.eventcatalog.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventResponse createEvent(EventRequest request, String organizerId) {
        Event.EventStatus status = Event.EventStatus.PUBLISHED;
        if (request.getStatus() != null) {
            try {
                status = Event.EventStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = Event.EventStatus.PUBLISHED;
            }
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .availableSeats(request.getCapacity())
                .price(request.getPrice())
                .organizerId(organizerId)
                .imageUrl(request.getImageUrl())
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        event = eventRepository.save(event);
        return convertToEventResponse(event);
    }

    public EventResponse updateEvent(String id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setImageUrl(request.getImageUrl());

        if (request.getStatus() != null) {
            try {
                event.setStatus(Event.EventStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        event.setUpdatedAt(LocalDateTime.now());

        event = eventRepository.save(event);
        return convertToEventResponse(event);
    }

    public EventResponse getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToEventResponse(event);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category).stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByOrganizer(String organizerId) {
        return eventRepository.findByOrganizerId(organizerId).stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByEventDateAfter(LocalDateTime.now()).stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> searchEvents(String query) {
        return eventRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    public EventResponse updateSeats(String id, Integer seatsToBook) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        // Decrease available seats
        int newAvailableSeats = event.getAvailableSeats() - seatsToBook;
        if (newAvailableSeats < 0) {
            throw new IllegalArgumentException("Cannot book more seats than available");
        }

        event.setAvailableSeats(newAvailableSeats);
        event.setUpdatedAt(LocalDateTime.now());
        event = eventRepository.save(event);

        return convertToEventResponse(event);
    }

    private EventResponse convertToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .availableSeats(event.getAvailableSeats())
                .price(event.getPrice())
                .organizerId(event.getOrganizerId())
                .imageUrl(event.getImageUrl())
                .status(event.getStatus().name())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}