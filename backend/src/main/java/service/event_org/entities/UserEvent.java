package service.event_org.entities;

import javax.persistence.*;

/**
 * Created By: Prashant Chaubey
 * Created On: 01-12-2019 14:47
 * Purpose: In context of sharing an user and an event have a many to many relationship. And this class represents a
 * table representing that relationship.
 **/
@Entity
@Table(name = "user_event")
public class UserEvent {

    /**
     * A primary key which is a combination of user id and event id.
     */
    @EmbeddedId
    private UserEventId id;

    /**
     * Related event
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private Event event;

    /**
     * Related user
     */
    @ManyToOne
    @MapsId("userId")
    private User user;

    /**
     * This flag represents that whether the shared event is archived by the user or not.
     */
    private boolean status;

    /**
     * For hibernate.
     */
    public UserEvent() {
    }

    public UserEvent(User user, Event event) {
        this.user = user;
        this.event = event;
        this.status = true;
        this.id = new UserEventId(user.getId(), event.getId());
    }

    public UserEventId getId() {
        return id;
    }

    public void setId(UserEventId id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEvent userEvent = (UserEvent) o;

        return id != null ? id.equals(userEvent.id) : userEvent.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
