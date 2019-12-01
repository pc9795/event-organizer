package service.event_org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import service.event_org.entities.User;
import service.event_org.repositories.UserRepository;
import service.event_org.utils.Constants;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:37
 * Purpose: Class used by spring security during auth.
 **/
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load a user by username from the database.
     *
     * @param username username
     * @return user
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format(Constants.ErrorMsg.USERNAME_NOT_FOUND, username));
        }
        return user;
    }
}
