package com.maraudersapp.android.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by joe on 10/16/15.
 */
public class UserCreationInfo {
    private String username, password, firstName, lastName;

    @JsonCreator
    public UserCreationInfo(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCreationInfo)) return false;

        UserCreationInfo that = (UserCreationInfo) o;

        return getUsername().equals(that.getUsername());

    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
