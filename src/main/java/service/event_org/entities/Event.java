package service.event_org.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:35
 * Purpose: Events created by user
 **/
@Entity
@Table(name = "events")
public class Event {
    /**
     * Auto incremented id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Title of the event.
     */
    @NotNull
    @Column(nullable = false)
    @Length(max = 50)
    private String title;

    /**
     * Description of the event
     */
    private String description;

    /**
     * Location of the event
     */
    private String location;

    /**
     * Start time of the event
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * End time of the event
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * user who created this event
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User createdBy;

    /**
     * The users to which this event is shared.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserEvent> sharedUsers = new HashSet<>();

    /**
     * Represents whether event is archived or not.
     */
    @JsonIgnore
    private boolean status;

    @JsonProperty
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty
    public User getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Get all the users to which this event is shared. It hides that this relationship has an intermediate class.
     *
     * @return users
     */
    @JsonProperty
    public Set<User> getSharedUsers() {
        return sharedUsers.stream().map(UserEvent::getUser).collect(Collectors.toSet());
    }

    /**
     * Share this event to a user
     *
     * @param sharedUser
     */
    public void addSharedUser(User sharedUser) {
        UserEvent userEvent = new UserEvent(sharedUser, this);
        this.sharedUsers.add(userEvent);
    }

    /**
     * Unshare this event to the user
     *
     * @param sharedUser
     */
    public void removeSharedUser(User sharedUser) {
        UserEvent userEvent = new UserEvent(sharedUser, this);
        this.sharedUsers.remove(userEvent);
    }

    /**
     * Archive this event for the given user with whom this event is shared.
     *
     * @param sharedUser
     */
    public void archiveSharedUser(User sharedUser) {
        UserEvent userEvent = new UserEvent(sharedUser, this);
        for (UserEvent sharedUserEvent : this.sharedUsers) {
            if (sharedUserEvent.equals(userEvent)) {
                sharedUserEvent.setStatus(false);
            }
        }
    }

    /**
     * Unarchive this event for the given user with whom this event is shared.
     *
     * @param sharedUser
     */
    public void unArchiveSharedUser(User sharedUser) {
        UserEvent userEvent = new UserEvent(sharedUser, this);
        for (UserEvent sharedUserEvent : this.sharedUsers) {
            if (sharedUserEvent.equals(userEvent)) {
                sharedUserEvent.setStatus(true);
            }
        }
    }
}
