package com.github.valhio.storeapi.utility;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.valhio.storeapi.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

/*
 *   This class is used to generate and validate JWT tokens.
 * */
@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(KBDA_LLC) // Who created the token
                .withAudience(KBDA_LLC_ADMINISTRATION) // Who is the token for
                .withIssuedAt(new Date()) // When was the token issued
                .withSubject(userPrincipal.getEmail()) // What is the subject of the token, in this case the email (unique identifier)
                .withArrayClaim(AUTHORITIES, claims) // What are the claims of the token, in this case, the authorities
                .withClaim("role", userPrincipal.getRole().name())
                .withClaim("email", userPrincipal.getEmail())
                .withClaim("userId", userPrincipal.getUserId())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // When does the token expire
                .sign(Algorithm.HMAC512(secretKey.getBytes())); // What is the secret key used to sign the token
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        String[] claims = this.getClaimsFromToken(token);
        List<SimpleGrantedAuthority> collect = stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        collect.add(new SimpleGrantedAuthority(this.getUserRole(token)));
        return collect;
    }

    public Authentication getAuthentication(String token, List<SimpleGrantedAuthority> authorities, HttpServletRequest request) {
        authorities.add(new SimpleGrantedAuthority(this.getUserRole(token)));
        User user = new User();
        user.setEmail(this.getSubject(token));
        user.setAuthorities(authorities.stream().map(GrantedAuthority::getAuthority).toArray(String[]::new));
        user.setUserId(this.getUserId(token));
        user.setRole(Role.valueOf(this.getUserRole(token)));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities); // Username, credentials, authorities. Credentials are not needed because we have already verified the token
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set the details of the request (IP address, session ID, etc)
//        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // Set the authentication in the Security Context
        return usernamePasswordAuthenticationToken; // Return the authentication
    }

    public boolean isTokenValid(String email, String token) {
        // Verify the token and check if the subject is the same as the email
        return !email.trim().equals("") && email.equals(getSubject(token)) && !isTokenExpired(getJWTVerifier(), token); // Check if the subject is the same as the email and if the token is not expired
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt(); // Get the expiration date from the token
        return expiration.before(new Date()); // Check if the expiration date is before the current date
    }

    private JWTVerifier getJWTVerifier() {
        // Create a verifier for the token using the secret key and issuer, and return it
        JWTVerifier verifier;
        try {
            verifier = JWT.require(Algorithm.HMAC512(secretKey)).withIssuer(KBDA_LLC).build();
        } catch (Exception e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    // Returns the claims of the user, in this case, the authorities
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        // Get the authorities from the user and return them as an array of strings
        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    // Returns the claims of the token, in this case, the authorities
    private String[] getClaimsFromToken(String token) {
        return getJWTVerifier() // Get the verifier
                .verify(token) // Verifies the token
                .getClaim(AUTHORITIES) // What are the claims of the token, in this case, the authorities
                .asArray(String.class);
    }

    // Returns the subject of the token, in this case the email
    public String getSubject(String token) {
        return getJWTVerifier().verify(token).getSubject();
    }

    public String getUserId(String token) {
        return getJWTVerifier().verify(token).getClaim("userId").asString();
    }

    public String getUserRole(String token) {
        return getJWTVerifier().verify(token).getClaim("role").asString();
    }

    public boolean isAdmin(String token) {
        return getJWTVerifier().verify(token).getClaim("role").asString().equals(Role.ROLE_ADMIN.name());
    }

    public boolean isSuperAdmin(String token) {
        return getJWTVerifier().verify(token).getClaim("role").asString().equals(Role.ROLE_SUPER_ADMIN.name());
    }

    public boolean hasRole(String token, Role[] roles) {
        return stream(roles)
                .anyMatch(role -> getJWTVerifier().verify(token).getClaim("role").asString().equals(role.name()));
    }


    // Does not work, no need for implementation
    public void invalidateToken(String token) {
        // Invalidate the token by setting the expiration date to the current date
        getJWTVerifier().verify(token).getExpiresAt().before(new Date());
    }

}
