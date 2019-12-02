package service.event_org.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created By: Prashant Chaubey
 * Created On: 01-12-2019 14:48
 * Purpose: In context of sharing an user and an event have a many to many relationship. And this class represents a
 * primary key for the table representing that relationship.
 **/
@Embeddable
public class UserEventId implements Serializable {
    /**
     * Id of the user
     */
    private Long userId;
    /**
     * Id of the shared event
     */
    private Long eventId;

    /**
     * For hibernate
     */
    public UserEventId() {
    }

    public UserEventId(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEventId that = (UserEventId) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return eventId != null ? eventId.equals(that.eventId) : that.eventId == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        return result;
    }
}
