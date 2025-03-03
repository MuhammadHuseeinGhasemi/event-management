package co.muhu.eventManagement.controller;

import co.muhu.eventManagement.entity.Event;
import co.muhu.eventManagement.exception.ResourceNotFoundException;
import co.muhu.eventManagement.model.EventDto;
import co.muhu.eventManagement.model.EventRegistrationDto;
import co.muhu.eventManagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    public static final String EVENT_PATH ="/api/v1/event";
    public static final String EVENT_PATH_ID ="/api/v1/event/{eventId}";
    private final EventService eventService;

    @GetMapping(value = EVENT_PATH)
    public ResponseEntity<List<EventDto>> getAllEvents(){
        List<EventDto> allEvents = eventService.getAllEvents();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION,EVENT_PATH)
                .body(allEvents);
    }

    @GetMapping(value = EVENT_PATH_ID)
    public ResponseEntity<EventDto> getEventById(@PathVariable("eventId") Long eventId){
        EventDto event = eventService.getEventById(eventId)
                .orElseThrow(()->new ResourceNotFoundException("There is no event with this id:"+eventId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION,EVENT_PATH_ID)
                .body(event);
    }

    @PostMapping(value = EVENT_PATH)
    public ResponseEntity<EventDto> saveEvent(@Validated @RequestBody EventRegistrationDto event){
        event.getParticipantSet().forEach(System.out::println);
        EventDto savedEvent = eventService.createEvent(event);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION,EVENT_PATH+"/"+savedEvent.getId())
                .body(savedEvent);
    }

    @PutMapping(value = EVENT_PATH_ID)
    public ResponseEntity<EventDto> updateEvent(@Validated @RequestBody Event event ,
                                             @PathVariable Long eventId){
        EventDto updateEvent = eventService.updateEvent(eventId, event)
                .orElseThrow(()->new ResourceNotFoundException("There is no event with this id:"+eventId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.LOCATION,EVENT_PATH+"/"+eventId)
                .body(updateEvent);
    }

    @DeleteMapping(EVENT_PATH_ID)
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId){
        boolean isDeleted = eventService.deleteEventById(eventId);

        if (!isDeleted){
            throw new ResourceNotFoundException("There is no event with this id:"+eventId);
        }
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.LOCATION,EVENT_PATH+"/"+eventId)
                .build();
    }
}
