package com.maraudersapp.android.storage;

public class UserInfo {

    private String username, firstName, lastName;

    public UserInfo(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserInfo(String username) {
        this(username, "", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        return !(username != null ? !username.equals(userInfo.username) : userInfo.username != null);

    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
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
}
