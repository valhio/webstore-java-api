package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.valhio.storeapi.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users") // Specify the MongoDB collection name
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends Auditable<String> {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @Field("userPassword")
    private String password;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private Boolean acceptedTerms;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("userFailedLoginAttempts") // Set the desired field name for MongoDB
    private int failedLoginAttempts;

    @Field("userLastLoginDate")
    private LocalDateTime lastLoginDate;

    @Field("userLastLoginDateDisplay")
    private LocalDateTime lastLoginDateDisplay;

    @Field("userLockoutDate")
    @JsonProperty(value = "isActive")
    private boolean isActive;

    @Field("userLocked")
    @JsonProperty(value = "isNotLocked")
    private boolean isNotLocked;

    @Field("userRole")
    private Role role;

    @Field("userAuthorities")
    private String[] authorities = new String[]{};
}
