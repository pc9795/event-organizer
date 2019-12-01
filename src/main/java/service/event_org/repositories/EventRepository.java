package service.event_org.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import service.event_org.entities.Event;
import service.event_org.entities.User;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:36
 * Purpose: TODO:
 **/
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    List<Event> findAllByCreatedByAndTitleLikeAndStatusOrderByStartTimeAsc(Pageable pageable, User user, String searchStr,
                                                                           boolean status);

    List<Event> findAllByCreatedByAndStatusOrderByStartTimeAsc(Pageable pageable, User user, boolean status);

    List<Event> findAllBySharedUsersIsAndTitleLikeOrderByStartTimeAsc(Pageable pageable, User user, String searchStr);

    List<Event> findAllBySharedUsersIsOrderByStartTimeAsc(Pageable pageable, User user);

    Event findByIdAndStatus(long id, boolean status);

    Event findById(long id);
}

