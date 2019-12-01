package service.event_org.exceptions;

/**
 * Created By: Prashant Chaubey
 * Created On: 30-11-2019 23:46
 * Purpose: Exception when a user tried to access a forbidden resource.
 **/
public class ForbiddenResourceException extends Exception {
    public ForbiddenResourceException() {
    }

    public ForbiddenResourceException(String message) {
        super(message);
    }
}
