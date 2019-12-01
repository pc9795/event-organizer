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
import java.util.Optional;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:34
 * Purpose: TODO:
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

    @GetMapping
    public List<User> getUsers(@RequestParam(value = "search") String search, Pageable pageable) {
        return userRepository.findAllByUsernameIgnoreCaseStartsWith(pageable, search);
    }

    @GetMapping("/{user_id}")
    public User getUser(@PathVariable("user_id") long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User id:%s", userId));
        }
        return user;
    }

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
