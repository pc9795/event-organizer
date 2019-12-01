package service.event_org.exceptions;

/**
 * Created By: Prashant Chaubey
 * Created On: 30-11-2019 18:31
 * Purpose: TODO:
 **/
public class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException() {
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
