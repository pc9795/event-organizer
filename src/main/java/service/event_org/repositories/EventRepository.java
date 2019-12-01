package service.event_org.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import service.event_org.entities.Event;
import service.event_org.entities.User;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:36
 * Purpose: Repository for events
 **/
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    /**
     * Find all events created by given user with the given status and starting with the given search string. Given
     * pageable attribute controls offset and limit of results.
     *
     * @param pageable
     * @param user
     * @param searchStr
     * @param status
     * @return events
     */
    List<Event> findAllByCreatedByAndTitleLikeAndStatusOrderByStartTimeAsc(Pageable pageable, User user, String searchStr,
                                                                           boolean status);

    /**
     * Find all events created by given user with the given status. Given pageable attribute controls offset and limit
     * of results.
     *
     * @param pageable
     * @param user
     * @param status
     * @return events
     */
    List<Event> findAllByCreatedByAndStatusOrderByStartTimeAsc(Pageable pageable, User user, boolean status);

    /**
     * Find all events shared with given user with the given status and starting with the given search string. Given
     * pageable attribute controls offset and limit of results.
     *
     * @param pageable
     * @param userId
     * @param searchStr
     * @param status
     * @return events
     */
    @Query(value = "select e from User u, Event e, UserEvent ue where e.id=ue.id.eventId and ue.id.userId=u.id " +
            "and ue.status=:status and e.title like :searchStr and u.id=:userId order by e.startTime asc ")
    List<Event> findAllBySharedUsersIsAndTitleLikeOrderByStartTimeAsc(Pageable pageable, @Param("userId") Long userId,
                                                                      @Param("searchStr") String searchStr,
                                                                      @Param("status") boolean status);

    /**
     * Find all events shared with given user with the given status. Given pageable attribute controls offset and limit
     * of results.
     *
     * @param pageable
     * @param userId
     * @param status
     * @return events
     */
    @Query(value = "select e from User u, Event e, UserEvent ue where e.id=ue.id.eventId and ue.id.userId=u.id " +
            "and ue.status=:status and u.id=:userId order by e.startTime asc ")
    List<Event> findAllBySharedUsersIsOrderByStartTimeAsc(Pageable pageable, @Param("userId") Long userId,
                                                          @Param("status") boolean status);

    /**
     * Find a event with given id.
     *
     * @param id
     * @return event
     */
    Event findById(long id);
}

