package com.github.valhio.storeapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valhio.storeapi.domain.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.github.valhio.storeapi.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;

/*
  The JWTAccessDeniedHandler class is used to return a 401 error code to clients that try to access a protected resource without proper authorization.
  (They do not have the required role).

  This class implements the AccessDeniedHandler interface from Spring Security.
  It's responsible for handling access denied errors that occur when a client tries to access a protected resource
  without proper authorization.

  The handle method is called by Spring Security when access denied exception occurs.
  It creates an HttpResponse object with a status code of 401 (Unauthorized), and a message of "Access denied".
  It then writes this object to the response output stream in JSON format using the Jackson ObjectMapper.
 */
@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Create a new HttpResponse object with the 401 status code and the ACCESS_DENIED message.
        HttpResponse httpResponse = HttpResponse.builder()
                .statusCode(401)
                .status(HttpStatus.UNAUTHORIZED)
                .reason(HttpStatus.UNAUTHORIZED.getReasonPhrase().toUpperCase())
                .message(ACCESS_DENIED_MESSAGE)
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // Write the HttpResponse object to the response output stream.
        OutputStream outputStream = response.getOutputStream(); // Get the output stream from the response.
        ObjectMapper mapper = new ObjectMapper(); // Create a new ObjectMapper object.
        mapper.writeValue(outputStream, httpResponse); // Write the HttpResponse object to the output stream.
        outputStream.flush(); // Flush the output stream.    }
    }
}
