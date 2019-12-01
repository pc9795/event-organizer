package service.event_org.exceptions;

import netscape.security.ForbiddenTargetException;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import service.event_org.utils.Constants;
import service.event_org.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created By: Prashant Chaubey
 * Created On: 30-11-2019 20:45
 * Purpose: TODO:
 **/
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleResourceNotFound(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_NOT_FOUND, Constants.ErrorMsg.NOT_FOUND, response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException exc,
                                                      HttpServletResponse response) throws IOException {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Utils.createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, errors.toString(), response);
    }

    @ExceptionHandler(ForbiddenResourceException.class)
    public void handleForbiddenResourceException(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, Constants.ErrorMsg.UNAUTHORIZED, response);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    public void handleInvalidClientRequests(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, Constants.ErrorMsg.BAD_REQUEST, response);
    }

    @ExceptionHandler({BadCredentialsException.class, BadDataException.class,
            MissingServletRequestParameterException.class, HttpRequestMethodNotSupportedException.class})
    public void handleInvalidClientRequestsWithExcMessages(Exception e, HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
    }

    @ExceptionHandler(Exception.class)
    public void handleAll(Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        Utils.createJSONErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Constants.ErrorMsg.INTERNAL_SERVER_ERROR, response);
    }
}
