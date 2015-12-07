package com.maraudersapp.android.storage;

/**
 * POJO used to represent information about a user of the app.
 */
public class UserInfo {

    private String username, firstName, lastName;

    public UserInfo(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Creates a user with no first name or last name.
     */
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
