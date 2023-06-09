package com.github.valhio.storeapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valhio.storeapi.domain.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static com.github.valhio.storeapi.constant.SecurityConstant.FORBIDDEN_MESSAGE;

/*
  The JWTAuthenticationEntryPoint class is used to return a 403 error code to clients that try to access a protected resource without proper authentication.
  (Client is not logged in)

  This class handles authentication exceptions that occur when an unauthenticated user tries to access a protected resource.
  It extends the Http403ForbiddenEntryPoint class from Spring Security, which is a default implementation
  that returns a 403 status code to clients that try to access a protected resource without proper authentication.

  This class overrides the commence method that is called whenever an exception is thrown
  due to an unauthenticated user trying to access a resource that requires authentication.
  It creates a new HttpResponse object with a 403 status code and a forbidden message.
  It sets the content type to JSON and writes the HttpResponse object to the response output stream using an ObjectMapper object. The HttpResponse object contains the status code, status, reason, message, and timestamp.
 */
@Component
public class JWTAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    // This method is called whenever an exception is thrown due to an unauthenticated user trying to access a resource that requires authentication.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        // Create a new HttpResponse object with the 403 status code and the FORBIDDEN_MESSAGE message.
        HttpResponse httpResponse = HttpResponse.builder()
                .statusCode(403)
                .status(HttpStatus.FORBIDDEN)
                .reason(HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase())
                .message(FORBIDDEN_MESSAGE)
                .timeStamp(new Date())
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // Write the HttpResponse object to the response output stream.
        OutputStream outputStream = response.getOutputStream(); // Get the output stream from the response.
        ObjectMapper mapper = new ObjectMapper(); // Create a new ObjectMapper object.
        mapper.writeValue(outputStream, httpResponse); // Write the HttpResponse object to the output stream.
        outputStream.flush(); // Flush the output stream.
    }
}
