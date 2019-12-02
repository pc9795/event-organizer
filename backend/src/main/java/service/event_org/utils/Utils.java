package service.event_org.utils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created By: Prashant Chaubey
 * Created On: 29-11-2019 14:37
 * Purpose: Utility methods for the project
 **/
public final class Utils {
    private Utils() {
    }

    /**
     * Create a standard JSON message with given error code and message.
     *
     * @param errorCode
     * @param errorMessage
     * @return json
     */
    private static String createErrorJSON(int errorCode, String errorMessage) {
        ObjectNode errorNode = JsonNodeFactory.instance.objectNode();
        errorNode.put("code", errorCode);
        errorNode.put("message", errorMessage);
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.set("error", errorNode);
        return root.toString();
    }

    /**
     * Create a JSON error message from given code and message and write it to the given response.
     *
     * @param errorCode
     * @param errorMessage
     * @param response
     * @throws IOException
     */
    public static void createJSONErrorResponse(int errorCode, String errorMessage, HttpServletResponse response)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(errorCode);
        response.getWriter().write(Utils.createErrorJSON(errorCode, errorMessage));
    }
}


