package com.github.valhio.storeapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
    The JpaEnversConfiguration class is a configuration class that enables caching and JPA auditing in a Spring application.
    It has a method that returns a ModelMapper object, which is a library for mapping objects between different representations.
    The class also defines an auditor aware bean, which is used to track the user who created or modified an entity in the database.
    The SpringSecurityAuditorAware class implements the AuditorAware interface and provides the current user's email address
    for the auditor field of the entity. The annotation @EnableCaching enables caching for the application,
    while @EnableJpaAuditing enables auditing for JPA entities. The configuration is done through Java-based configuration
    instead of XML configuration.
*/

@Configuration
@EnableCaching
@EnableJpaAuditing
public class JpaEnversConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    AuditorAware<String> auditorAware() {
        return new SpringSecurityAuditorAware();
    }
}
