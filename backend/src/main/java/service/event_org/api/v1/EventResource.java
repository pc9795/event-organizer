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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:34
 * Purpose: REST resource for accessing Events
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

    /**
     * Get all active created events
     *
     * @param pageable
     * @param search
     * @param principal
     * @return events
     */
    @GetMapping
    public List<Event> getActiveEvents(Pageable pageable, @RequestParam(value = "search", required = false) String search,
                                       Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Event> createdEvents;
        List<Event> sharedEvents;
        //We have passed a search parameter
        //Status = true represents active events.
        if (search != null) {
            createdEvents = eventRepository.
                    findAllByCreatedByAndTitleContainingAndStatusOrderByStartTimeAsc(pageable, user, search, true);
            sharedEvents = eventRepository.
                    findAllBySharedUsersIsAndTitleContainingOrderByStartTimeAsc(pageable, user.getId(), search, true);
        } else {
            createdEvents = eventRepository.findAllByCreatedByAndStatusOrderByStartTimeAsc(pageable, user, true);
            sharedEvents = eventRepository.findAllBySharedUsersIsOrderByStartTimeAsc(pageable, user.getId(), true);
        }
        //Sorting and combining the list.
        createdEvents.addAll(sharedEvents);
        createdEvents.sort(Comparator.comparing(Event::getStartTime));

        return createdEvents;
    }

    /**
     * Get all created archived events
     *
     * @param pageable
     * @param search
     * @param principal
     * @return events
     */
    @GetMapping("/archive")
    public List<Event> getArchivedEvents(Pageable pageable, @RequestParam(value = "search", required = false)
            String search, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Event> archivedEvents;
        List<Event> sharedArchivedEvents;
        //We have passed a search parameter
        //Status = false represents archived events.
        if (search != null) {
            archivedEvents = eventRepository.
                    findAllByCreatedByAndTitleContainingAndStatusOrderByStartTimeAsc(pageable, user, search, false);
            sharedArchivedEvents = eventRepository.
                    findAllBySharedUsersIsAndTitleContainingOrderByStartTimeAsc(pageable, user.getId(), search, false);
        } else {
            archivedEvents = eventRepository.findAllByCreatedByAndStatusOrderByStartTimeAsc(pageable, user, false);
            sharedArchivedEvents = eventRepository.findAllBySharedUsersIsOrderByStartTimeAsc(pageable, user.getId(),
                    false);
        }
        //Sorting and combining the combined list
        archivedEvents.addAll(sharedArchivedEvents);
        archivedEvents.sort(Comparator.comparing(Event::getStartTime));

        return archivedEvents;
    }

    /**
     * Get an event
     *
     * @param eventId
     * @param principal
     * @return event
     * @throws ResourceNotFoundException  resource with that id doesn't exists
     * @throws ForbiddenResourceException the event is neither created or shared with current user.
     */
    @GetMapping("/{event_id}")
    public Event getEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        User user = userRepository.findByUsername(principal.getName());
        //Either this event is created by the user or shared to him.
        if (!event.getCreatedBy().getUsername().equals(user.getUsername()) && !event.getSharedUsers().contains(user)) {
            throw new ForbiddenResourceException();
        }
        return event;
    }

    /**
     * Create an event
     *
     * @param event
     * @param principal
     * @return event
     * @throws BadDataException start date is after the end date.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@Valid @RequestBody Event event, Principal principal) throws BadDataException {
        //start date is after end date
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new BadDataException("End time can't be after start time");
        }
        User user = userRepository.findByUsername(principal.getName());
        //Mark event as active
        event.setStatus(true);
        event.setCreatedBy(user);
        return eventRepository.save(event);
    }

    /**
     * Update an event
     *
     * @param eventId
     * @param event
     * @param principal
     * @return updated event
     * @throws ResourceNotFoundException  event with given id is not present
     * @throws ForbiddenResourceException event is not created by the current user
     * @throws BadDataException           event is archived
     */
    @PutMapping("/{event_id}")
    public Event updateEvent(@PathVariable("event_id") long eventId, @Valid @RequestBody Event event, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException, BadDataException {
        //start date is after end date
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new BadDataException("End time can't be after start time");
        }
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        //Check event is created by current user
        if (!dbEvent.getCreatedBy().getUsername().equals(principal.getName())) {
            throw new ForbiddenResourceException();
        }
        //Check event is not archived
        if (!dbEvent.getStatus()) {
            throw new BadDataException("Can't edit an archived event");
        }
        //Update
        dbEvent.setTitle(event.getTitle());
        dbEvent.setDescription(event.getDescription());
        dbEvent.setStartTime(event.getStartTime());
        dbEvent.setEndTime(event.getEndTime());
        dbEvent.setLocation(event.getLocation());
        //Save the updated state
        return eventRepository.save(dbEvent);
    }

    /**
     * Delete the given event
     *
     * @param eventId
     * @param principal
     * @throws ResourceNotFoundException  event with given id is not present
     * @throws ForbiddenResourceException event is not created by currentFuser
     */
    @DeleteMapping("/{event_id}")
    public void deleteEvent(@PathVariable("event_id") long eventId, Principal principal) throws ResourceNotFoundException,
            ForbiddenResourceException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        //Check event is created by current user
        if (!dbEvent.getCreatedBy().getUsername().equals(principal.getName())) {
            throw new ForbiddenResourceException();
        }
        //Delete
        eventRepository.delete(dbEvent);
    }

    /**
     * Archive a given event
     *
     * @param eventId
     * @param principal
     * @return updated event
     * @throws ForbiddenResourceException event is not created by the user
     * @throws ResourceNotFoundException  event with given id is not present
     */
    @PatchMapping("/{event_id}/archive")
    public Event archiveEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ForbiddenResourceException, ResourceNotFoundException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        //Check event is created by current user
        if (!dbEvent.getCreatedBy().getUsername().equals(principal.getName())) {
            throw new ForbiddenResourceException();
        }
        //Mark as archived
        dbEvent.setStatus(false);
        //Save the updated event
        return eventRepository.save(dbEvent);
    }

    /**
     * Unarchive a given event
     *
     * @param eventId
     * @param principal
     * @return updated event
     * @throws ForbiddenResourceException event is not created by the user
     * @throws ResourceNotFoundException  event with given id is not present
     */
    @PatchMapping("/{event_id}/unarchive")
    public Event unArchiveEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ForbiddenResourceException, ResourceNotFoundException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        //Check event is create by current user
        if (!dbEvent.getCreatedBy().getUsername().equals(principal.getName())) {
            throw new ForbiddenResourceException();
        }
        //Mark as active
        dbEvent.setStatus(true);
        //Save the updated event
        return eventRepository.save(dbEvent);
    }

    /**
     * Archive a shared event
     *
     * @param eventId
     * @param principal
     * @return updated event
     * @throws ForbiddenResourceException event is not shared with the user
     * @throws ResourceNotFoundException  event with given id is not present
     */
    @PatchMapping("/{event_id}/shared/archive")
    public Event archiveSharedEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ForbiddenResourceException, ResourceNotFoundException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        User user = userRepository.findByUsername(principal.getName());
        //Check event is shared with the user or not
        if (!dbEvent.getSharedUsers().contains(user)) {
            throw new ForbiddenResourceException();
        }
        //Mark the shared event as archived
        dbEvent.archiveSharedUser(user);
        //Save the updated event
        return eventRepository.save(dbEvent);
    }

    /**
     * Unarchive a shared event
     *
     * @param eventId
     * @param principal
     * @return updated event
     * @throws ForbiddenResourceException event is not shared with the user
     * @throws ResourceNotFoundException  event with given id is not present
     */
    @PatchMapping("/{event_id}/shared/unarchive")
    public Event unArchiveSharedEvent(@PathVariable("event_id") long eventId, Principal principal)
            throws ForbiddenResourceException, ResourceNotFoundException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        User user = userRepository.findByUsername(principal.getName());
        //Check event is shared with this user
        if (!dbEvent.getSharedUsers().contains(user)) {
            throw new ForbiddenResourceException();
        }
        //Mark the shared event as unarchived
        dbEvent.unArchiveSharedUser(user);
        //Save the updated event
        return eventRepository.save(dbEvent);
    }

    /**
     * Share the given event with a list of users
     *
     * @param principal
     * @return updated event
     * @throws ResourceNotFoundException  either user or event with given id is not present
     * @throws ForbiddenResourceException if event is not created by user
     * @throws BadDataException           event is archived
     */
    @PostMapping("/share/{username}")
    public List<Event> shareEvent(@PathVariable("username") String username, @RequestBody Long[] eventIds, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException, BadDataException {
        if (principal.getName().equals(username)) {
            throw new BadDataException("Can't share with yourself");
        }
        User dbUser = userRepository.findByUsername(username);
        if (dbUser == null) {
            throw new ResourceNotFoundException(String.format("Username:%s", username));
        }
        //Remove the  null ids.
        List<Long> eventIdsList = Arrays.stream(eventIds).filter(Objects::nonNull).collect(Collectors.toList());

        //Early exit if nothing is there
        if (eventIdsList.size() == 0) {
            throw new BadDataException("No data received");
        }
        List<Event> updatedEvents = new ArrayList<>();
        for (Long eventId : eventIdsList) {
            Event dbEvent = eventRepository.findById(eventId.longValue());
            //Check event is archived
            if (!dbEvent.getStatus()) {
                throw new BadDataException("Can't share an archived event");
            }
            //Check event is created by current user
            if (!dbEvent.getCreatedBy().getUsername().equals(principal.getName())) {
                throw new ForbiddenResourceException();
            }
            dbEvent.addSharedUser(dbUser);
            eventRepository.save(dbEvent);
            updatedEvents.add(dbEvent);
        }
        //Save the updated user
        return updatedEvents;
    }

    /**
     * Unshare an event with a user
     *
     * @param eventId
     * @param userId
     * @param principal
     * @return updated event
     * @throws ResourceNotFoundException  either event with given id is not present
     * @throws ForbiddenResourceException if event is not created by the user
     */
    @DeleteMapping("/{event_id}/unshare/{user_id}")
    public Event unShareEvent(@PathVariable("event_id") long eventId, @PathVariable("user_id") Long userId,
                              Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException, BadDataException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        User sharedUser = userRepository.findById(userId.longValue());
        if (sharedUser == null) {
            throw new ResourceNotFoundException(String.format("User id:%s", eventId));
        }
        // Check event is created by the user
        if (!dbEvent.getCreatedBy().getUsername().equals(principal.getName())) {
            throw new ForbiddenResourceException();
        }
        //Unshare the event with given user
        dbEvent.removeSharedUser(sharedUser);
        //Save the updated event
        return eventRepository.save(dbEvent);
    }

    /**
     * Unshare an event with the current user
     *
     * @param eventId
     * @param principal
     * @return updated event
     * @throws ResourceNotFoundException  either event with given id is not present
     * @throws ForbiddenResourceException if event is not created by the user
     */
    @DeleteMapping("/{event_id}/unshare")
    public Event unShareEventWithCurrentUser(@PathVariable("event_id") long eventId, Principal principal)
            throws ResourceNotFoundException, ForbiddenResourceException {
        Event dbEvent = eventRepository.findById(eventId);
        if (dbEvent == null) {
            throw new ResourceNotFoundException(String.format("Event id:%s", eventId));
        }
        User user = userRepository.findByUsername(principal.getName());
        // Check event is shared by the user
        if (!dbEvent.getSharedUsers().contains(user)) {
            throw new ForbiddenResourceException();
        }
        //Unshare the event with current user
        dbEvent.removeSharedUser(user);
        //Save the updated event
        return eventRepository.save(dbEvent);
    }
}
