package com.github.valhio.storeapi.listener;


import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

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
