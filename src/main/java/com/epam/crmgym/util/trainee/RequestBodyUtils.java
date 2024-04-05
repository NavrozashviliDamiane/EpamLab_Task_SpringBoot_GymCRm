package com.epam.crmgym.util.trainee;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestBodyUtils {

    public static final String REQUEST_BODY_ATTRIBUTE = "requestBody";

    public static void storeRequestBody(HttpServletRequest request, String requestBody) {
        request.setAttribute(REQUEST_BODY_ATTRIBUTE, requestBody);
    }

    public static String getRequestBody(HttpServletRequest request) {
        return (String) request.getAttribute(REQUEST_BODY_ATTRIBUTE);
    }
}