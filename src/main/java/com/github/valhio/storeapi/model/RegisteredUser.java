package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any unknown properties that may be sent in the request
public class RegisteredUser extends User{
    //    @NotNull(message = "Password cannot be empty")
//    @Length(min = 7, message = "Password should be at least 7 characters long")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private int failedLoginAttempts;
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLoginDateDisplay;
    private Boolean isRegistered;
}
