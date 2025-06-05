package org.example.domain;

import java.util.List;

public class Drawing {
    private String name;
    private String username;
    private String description;


    public Drawing() {

    }

    public Drawing(String name, String username, String description) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
