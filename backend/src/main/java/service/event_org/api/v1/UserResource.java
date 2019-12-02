package service.event_org.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import service.event_org.entities.User;
import service.event_org.exceptions.ResourceNotFoundException;
import service.event_org.exceptions.UserAlreadyExistException;
import service.event_org.repositories.UserRepository;
import service.event_org.utils.Constants;

import javax.validation.Valid;
import java.util.List;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:34
 * Purpose: REST resource for accessing Users.
 **/
@RestController
@RequestMapping("/api/v1/users")
public class UserResource {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserResource(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all users whose name starts with the given search parameter.
     *
     * @param search
     * @param pageable
     * @return users
     */
    @GetMapping
    public List<User> getUsers(@RequestParam(value = "search") String search, Pageable pageable) {
        return userRepository.findAllByUsernameIgnoreCaseStartsWith(pageable, search);
    }

    /**
     * Get a user with given id.
     *
     * @param userId
     * @return
     * @throws ResourceNotFoundException user with given user id not present
     */
    @GetMapping("/{user_id}")
    public User getUser(@PathVariable("user_id") long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User id:%s", userId));
        }
        return user;
    }

    /**
     * Create a new user
     *
     * @param user
     * @return
     * @throws UserAlreadyExistException a user with given username already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) throws UserAlreadyExistException {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistException(String.format(Constants.ErrorMsg.USER_ALREADY_EXIST,
                    user.getUsername()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
