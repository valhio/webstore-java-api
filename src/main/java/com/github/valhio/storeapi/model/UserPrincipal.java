package com.github.valhio.storeapi.model;

import com.github.valhio.storeapi.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
    The UserPrinciple class is used to represent the user in the application (Spring Security).

    The UserPrincipal class is a UserDetails implementation used to represent the user in the application's security context,
    specifically in Spring Security. It is essentially a wrapper around the User entity and provides the necessary information
    to Spring Security for authentication and authorization purposes.

    This class has three methods that provide access to user information, namely getRole(), getEmail(), and getUserId().
    These methods return the corresponding values from the User entity.

    The getAuthorities() method returns a collection of GrantedAuthority objects that represent the user's authorities.
    This method is used by Spring Security to determine whether the user has access to specific endpoints in the application.

    The getPassword() method returns the user's password. However, this method is usually not used
    because the password is typically not stored in plain text but rather encrypted in the database.

    The getUsername() method returns the user's email address, which serves as the username for authentication purposes.

    The remaining methods (isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), and isEnabled()) return boolean values
    that indicate whether the user account is valid and can be used for authentication and authorization.

    Overall, the UserPrincipal class provides the necessary user information for Spring Security to
    authenticate and authorize the user in the application.

 * */
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private User user;

    public Role getRole() {
        return user.getRole();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getUserId() {
        return user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = Arrays.stream(this.user.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(this.user.getRole().name())); // Add the user's role to the list of authorities, so that Spring Security can use it to authorize the user to access the endpoints that require that role (Ex. @PreAuthorize("hasRole('ROLE_ADMIN')"))
        return simpleGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }
}
