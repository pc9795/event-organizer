package service.event_org.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import service.event_org.entities.User;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:36
 * Purpose: TODO:
 **/
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAllByUsernameIgnoreCaseStartsWith(Pageable pageable, String searchStr);

    User findById(long id);
}
