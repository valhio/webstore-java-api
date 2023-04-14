package com.github.valhio.storeapi.listener;

import com.github.valhio.storeapi.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;


/*
    The AuthenticationFailureListener class is a listener that listens for authentication failure events (AuthenticationFailureBadCredentialsEvent).
    When an authentication failure event occurs, the onAuthenticationFailure method is called automatically by Spring's event handling mechanism,
    passing the 'AuthenticationFailureBadCredentialsEvent' object as an argument.
    The method extracts the username of the user who failed to log in and calls the loginFailed method
    of the LoginAttemptService class to increment the number of failed login attempts for that user.
 */
@Component
public class AuthenticationFailureListener {

    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
//        WebAuthenticationDetails auth = (WebAuthenticationDetails) event.getAuthentication().getDetails(); // Get the IP address of the user who failed to log in.
//        WebAuthenticationDetails username = (WebAuthenticationDetails) event.getAuthentication().getPrincipal(); // Get the username of the user who failed to log in.
        String username = event.getAuthentication().getName(); // Get the username of the user who failed to log in.
        loginAttemptService.loginFailed(username);
    }
}
