package com.maraudersapp.android.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joe on 10/16/15.
 */
public class UserCreationInfo implements JsonSchema {
    private String username, password, firstName, lastName;

    public UserCreationInfo(String username, String password, String firstName, String lastName) {
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
    public String writeToJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
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
