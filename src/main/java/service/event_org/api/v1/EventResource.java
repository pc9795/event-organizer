package service.event_org.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.event_org.entities.Event;
import service.event_org.entities.User;
import service.event_org.exceptions.BadDataException;
import service.event_org.exceptions.ForbiddenResourceException;
import service.event_org.exceptions.ResourceNotFoundException;
import service.event_org.repositories.EventRepository;
import service.event_org.repositories.UserRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:34
 * Purpose: TODO:
 **/
@RestController
@RequestMapping("/api/v1/events")
public class EventResource {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventResource(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Event> getActiveEvents(Pageable pageable, @RequestParam(value = "search", required = false) String search,
                                       Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Event> createdEvents;
        if (search != null) {
            createdEvents = eventRepository.
                    findAllByCreatedByAndTitleLikeAndStatusOrderByStartTimeAsc(pageable, user, search, true);
        } else {
            createdEvents = eventRepository.findAllByCreatedByAndStatusOrderByStartTimeAsc(pageable, user, true);
        }
        return createdEvents;
    }

    @GetMapping("/shared")
    public List<Event> getSharedEvents(Pageable pageable, @RequestParam(value = "search", required = false)
            String search, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Event> sharedEvents;
        if (search != null) {
            sharedEvents = eventRepository.
                    findAllBySharedUsersIsAndTitleLikeOrderByStartTimeAsc(pageable, user, search);
        } else {
            sharedEvents = eventRepository.findAllBySharedUsersIsOrderByStartTimeAsc(pageable, user);
        }
        return sharedEvents;
    }

    @GetMapping("/archive")
    public List<Event> getArchivedEvents(Pageable pageable, @RequestParam(value = "search", required = false)
            String search, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Event> archivedEvents;
        if (search != null) {
            archivedEvents = eventRepository.
                    findAllByCreatedByAndTitleLikeAndStatusOrderByStartTimeAsc(pageable, user, search, false);
        } else {
            archivedEvents = eventRepository.findAllByCreatedByAndStatusOrderByStartTimeAsc(pageable, user, false);
        }
        return archivedEvents;
    }

    @GetMapping("/{event_id}")
    public Event getEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException();
        }
        User user = userRepository.findByUsername(principal.getName());
        if (!event.getCreatedBy().getUsername().equals(user.getUsername()) && !event.getSharedUsers().contains(user)) {
            throw new ForbiddenResourceException();
        }
        return event;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@Valid @RequestBody Event event, Principal principal) throws BadDataException {
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new BadDataException("End time can't be after start time");
        }
        User user = userRepository.findByUsername(principal.getName());
        event.setStatus(true);
        user.addEvent(event);
        return eventRepository.save(event);
    }

    @PutMapping("/{event_id}")
    public Event updateEvent(@PathVariable("event_id") long eventId, @Valid @RequestBody Event event, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException, BadDataException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException();
        }
        if (!dbEvent.getStatus()) {
            throw new BadDataException("Can't edit an archived event");
        }
        User user = userRepository.findByUsername(principal.getName());
        if (!dbEvent.getCreatedBy().getUsername().equals(user.getUsername())) {
            throw new ForbiddenResourceException();
        }
        dbEvent.setTitle(event.getTitle());
        dbEvent.setDescription(event.getDescription());
        dbEvent.setStartTime(event.getStartTime());
        dbEvent.setEndTime(event.getEndTime());
        dbEvent.setLocation(event.getLocation());
        return eventRepository.save(dbEvent);
    }

    @DeleteMapping("/{event_id}")
    public void deleteEvent(@PathVariable("event_id") long eventId, Principal principal) throws ResourceNotFoundException,
            ForbiddenResourceException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException();
        }
        User user = userRepository.findByUsername(principal.getName());
        if (!dbEvent.getCreatedBy().getUsername().equals(user.getUsername())) {
            throw new ForbiddenResourceException();
        }
        eventRepository.delete(dbEvent);
    }


    //todo implement archival of shared events.
    @PatchMapping("/{event_id}/archive")
    public Event archiveEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ForbiddenResourceException, ResourceNotFoundException, BadDataException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException();
        }
        if (!dbEvent.getStatus()) {
            throw new BadDataException("Event already archived");
        }
        User user = userRepository.findByUsername(principal.getName());
        if (!dbEvent.getCreatedBy().getUsername().equals(user.getUsername())) {
            throw new ForbiddenResourceException();
        }
        dbEvent.setStatus(false);
        return eventRepository.save(dbEvent);
    }

    @PatchMapping("/{event_id}/share")
    public Event shareEvent(@PathVariable("event_id") long eventId, @RequestBody Long[] userIds, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException, BadDataException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException();
        }
        if (!dbEvent.getStatus()) {
            throw new BadDataException("Can't share an archived event");
        }
        User user = userRepository.findByUsername(principal.getName());
        if (!dbEvent.getCreatedBy().getUsername().equals(user.getUsername())) {
            throw new ForbiddenResourceException();
        }
        List<Long> userIdsList = Arrays.stream(userIds).filter(Objects::nonNull).collect(Collectors.toList());
        if (userIdsList.size() == 0) {
            return dbEvent;
        }
        for (Long userId : userIdsList) {
            if (userId == null) {
                continue;
            }
            User sharedUser = userRepository.findById(userId.longValue());
            if (sharedUser == null) {
                throw new ResourceNotFoundException();
            }
            dbEvent.addSharedUser(sharedUser);
        }
        return eventRepository.save(dbEvent);
    }

}
