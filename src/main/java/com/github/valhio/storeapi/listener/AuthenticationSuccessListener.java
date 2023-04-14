package com.github.valhio.storeapi.listener;


import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/*
    The AuthenticationSuccessListener class is a listener that listens for authentication success events (AuthenticationSuccessEvent).
    When an authentication success event occurs, the onAuthenticationSuccess method is called automatically by Spring's event handling mechanism,
    passing the 'AuthenticationSuccessEvent' object as an argument.
    The method extracts the username of the user who successfully logged in and calls the loginSucceeded method
    of the LoginAttemptService class to reset the number of failed login attempts for that user.
 */

@Component
public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
//        event.getAuthentication().getDetails(); // Get the IP address of the user who successfully logged in.
//        String username = event.getAuthentication().getName();// Get the username of the user who successfully logged in.
        Object user = event.getAuthentication().getPrincipal(); // Get the email of the user who successfully logged in.
        if (user instanceof UserPrincipal) loginAttemptService.loginSucceeded(((UserPrincipal) user).getEmail());
    }

}
