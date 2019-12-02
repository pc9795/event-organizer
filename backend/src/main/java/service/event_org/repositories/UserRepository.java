package service.event_org.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import service.event_org.entities.User;

import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:36
 * Purpose: Repository for users
 **/
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    /**
     * Find a user by username
     *
     * @param username
     * @return user
     */
    User findByUsername(String username);

    /**
     * Find all users with user names starting with given search string. Given pageable attribute controls the offset
     * and limit of results.
     *
     * @param pageable
     * @param searchStr
     * @return users
     */
    List<User> findAllByUsernameIgnoreCaseStartsWith(Pageable pageable, String searchStr);

    /**
     * Find a user by id
     *
     * @param id
     * @return user
     */
    User findById(long id);
}
