package org.example.domain;

import java.util.List;

public class Drawing {
    private String name;
    private List<String> username;
    private String description;
    private List<String> pendingRequests;

    public Drawing() {

    }

    public Drawing(String name, List<String> username, String description) {
        this.name = name;
        this.username = username;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getPendingRequests() {
        return pendingRequests;
    }
    public void setPendingRequests(List<String> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }
}
