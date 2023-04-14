package com.github.valhio.storeapi.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/*
    The SpringSecurityAuditorAware class is an implementation of the AuditorAware interface in Spring Data,
    which is used to automatically set auditing fields such as createdBy and lastModifiedBy
    when inserting or updating entities in a database. This implementation retrieves the currently authenticated user
    from the Spring Security context using the SecurityContextHolder, and returns it as an Optional<String>.
    If there is no authenticated user, the Optional will be empty. This implementation is used in conjunction with
    the @CreatedBy and @LastModifiedBy annotations in entity classes to automatically set these fields with the
    username (or in this api, the email address) of the currently authenticated user.
 */

public class SpringSecurityAuditorAware implements AuditorAware<String> {
//    https://stackoverflow.com/questions/66498894/spring-boot-auditing-map-current-user-to-createdby-lastmodifiedby
//    https://www.youtube.com/watch?v=LE_oknOU_U0&list=PL9l1zUfnZkZnfOFgWa4K9lTzhvCkjTQfm&index=57

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return Optional.ofNullable(username);
    }
}