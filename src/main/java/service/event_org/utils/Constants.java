package service.event_org.utils;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:37
 * Purpose: TODO:
 **/
public final class Constants {
    private Constants() {
    }

    public class ErrorMsg {
        public static final String USERNAME_NOT_FOUND = "User with the username:%s is not found";
        public static final String PASSWORDS_NOT_MATCH = "Passwords not match";
        public static final String USER_ALREADY_EXIST = "User with the username:%s already exists";
        public static final String INTERNAL_SERVER_ERROR = "Something bad happened";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String FORBIDDEN_RESOURCE = "Forbidden Resource";
        public static final String BAD_REQUEST = "Bad request";
        public static final String NOT_FOUND = "Not found";
    }
}
