package com.github.valhio.storeapi.filter;

import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.github.valhio.storeapi.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.github.valhio.storeapi.constant.SecurityConstant.TOKEN_PREFIX;

/*
    The JWTAuthorizationFilter class is used to validate the JWT token sent by the client in the Authorization header of the request.

    This class extends the OncePerRequestFilter class. Its purpose is to filter incoming requests
    to validate the JSON Web Token (JWT) sent by the client in the Authorization header of the request.

    OncePerRequestFilter is a Spring Security filter that is used to filter incoming requests to validate the JSON Web Token (JWT)
    sent by the client in the Authorization header of the request.

    When a request is received, the doFilterInternal method is called.
    The method first checks if the request method is OPTIONS. If it is, the method sets the response status to HttpStatus.OK.
    OPTIONS is a preflight request sent by the browser to the server to check if the origin is allowed to make the actual request.
    If the request method is not OPTIONS, the method extracts the JWT from the Authorization header of the request.

    If the JWT is not present or does not start with the TOKEN_PREFIX constant,
    the filter passes the request to the next filter in the chain without any further processing.
    If the JWT is present and starts with the TOKEN_PREFIX constant,
    the filter uses the JWTTokenProvider to validate the token and extract the email and authorities from it.

    If the email is not null and the security context is empty, the filter sets the authentication in the security context.
    If the token is not valid, the security context is cleared.
    Finally, the filter passes the request to the next filter in the chain.

    In summary, the JWTAuthorizationFilter is responsible for validating the JWT sent by the client in the Authorization header of the request
    and setting the authentication in the security context if the JWT is valid.
*/

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;

    public JWTAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) { // if the request is OPTIONS, then we allow it
            response.setStatus(HttpStatus.OK.value()); // we set the status to OK
        } else { // if the request is not OPTIONS, then we check if the request has a token
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // we get the "Authorization" header field

            // if the header is null or doesn't start with Bearer, we pass the request to the next filter
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorizationHeader.substring(TOKEN_PREFIX.length()); // we get the token from the header
            String email = jwtTokenProvider.getSubject(token); // we get the email from the token

            // if the email is not null and the security context is empty, we set the authentication
//            if (jwtTokenProvider.isTokenValid(email, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtTokenProvider.isTokenValid(email, token)) {
                List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token); // we get the authorities from the token
                Authentication authentication = jwtTokenProvider.getAuthentication(token, authorities, request); // we get the authentication from the token
                SecurityContextHolder.getContext().setAuthentication(authentication); // we set the authentication in the security context
            } else {
                SecurityContextHolder.clearContext(); // if the token is not valid, we clear the security context
            }
            filterChain.doFilter(request, response); // we pass the request to the next filter
        }
    }
}
