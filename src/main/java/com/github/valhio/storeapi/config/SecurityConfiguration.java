package com.github.valhio.storeapi.config;

import com.github.valhio.storeapi.filter.JWTAccessDeniedHandler;
import com.github.valhio.storeapi.filter.JWTAuthenticationEntryPoint;
import com.github.valhio.storeapi.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.github.valhio.storeapi.constant.SecurityConstant.PUBLIC_URLS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/*
 *   This class is responsible for configuring the security of the application.
 * */
@Configuration
@EnableWebSecurity() // This annotation enables the Spring Security module.
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final JWTAccessDeniedHandler jwtAccessDeniedHandler;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    // ATTENTION: If updating the dependencies, you will need to add the new additions to the test files' @Import annotation,
    // where the SecurityConfiguration class is used/imported.
    // Otherwise, the tests will fail.
    @Autowired
    public SecurityConfiguration(JWTAccessDeniedHandler jwtAccessDeniedHandler, JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint, JWTAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler) // Fires when user tries to access a resource without having needed permission/s (role/s)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Fires when user tries to access a protected resource without supplying any credentials (Is not logged in)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, AuthorizationFilter.class)
                .authorizeHttpRequests()
                .antMatchers(PUBLIC_URLS).permitAll() // Permit all requests to PUBLIC_URLS
                .anyRequest().authenticated()
                .and()
                .logout().logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // logoutRequestMatcher(new AntPathRequestMatcher("/logout")) is used to specify the URL to logout
                .logoutSuccessUrl("/login").permitAll()
                .and()
//                .userDetailsService(customUserDetailsService)
//                .passwordEncoder(bCryptPasswordEncoder)
                .exceptionHandling().accessDeniedPage("/accessDenied");
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
