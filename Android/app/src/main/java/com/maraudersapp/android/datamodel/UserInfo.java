package com.maraudersapp.android.datamodel;

/**
 * Created by Matthew on 10/16/2015.
 */
public class UserInfo {
    private String username;
    private String firstName;
    private String lastName;

    public UserInfo(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
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
        if (!(o instanceof UserInfo)) return false;

        UserInfo userInfo = (UserInfo) o;

        if (!getUsername().equals(userInfo.getUsername())) return false;
        if (!getFirstName().equals(userInfo.getFirstName())) return false;
        return getLastName().equals(userInfo.getLastName());

    }

    @Override
    public int hashCode() {
        int result = getUsername().hashCode();
        result = 31 * result + getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
