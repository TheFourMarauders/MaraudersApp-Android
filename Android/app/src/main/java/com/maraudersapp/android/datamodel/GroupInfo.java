package com.maraudersapp.android.datamodel;

import java.util.Set;

/**
 * Created by Matthew on 10/16/2015.
 */
public class GroupInfo {
    private Set<String> members;
    private String id;
    private String name;

    public GroupInfo(Set<String> members, String id, String name) {
        this.members = members;
        this.id = id;
        this.name = name;
    }

    public Set<String> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupInfo)) return false;

        GroupInfo groupInfo = (GroupInfo) o;

        if (!getMembers().equals(groupInfo.getMembers())) return false;
        if (!getId().equals(groupInfo.getId())) return false;
        return getName().equals(groupInfo.getName());

    }

    @Override
    public int hashCode() {
        int result = getMembers().hashCode();
        result = 31 * result + getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "members=" + members +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
