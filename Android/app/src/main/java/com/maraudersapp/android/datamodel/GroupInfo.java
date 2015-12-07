package com.maraudersapp.android.datamodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Created by Matthew on 10/16/2015.
 *
 * Class for group info. contains members, id and name
 */
public class GroupInfo {
    private Set<String> members;
    private String _id;
    private String name;

    @JsonCreator

    public GroupInfo(
            @JsonProperty("members") Set<String> members,
            @JsonProperty("_id") String _id,
            @JsonProperty("name") String name) {
        this.members = members;
        this._id = _id;
        this.name = name;
    }

    public Set<String> getMembers() {
        return members;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupInfo)) return false;

        GroupInfo groupInfo = (GroupInfo) o;

        return get_id().equals(groupInfo.get_id());

    }

    @Override
    public int hashCode() {
        return get_id().hashCode();
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "members=" + members +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
