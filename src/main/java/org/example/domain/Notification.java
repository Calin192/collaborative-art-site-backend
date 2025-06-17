package org.example.domain;

public class Notification {
    private String drawingName;
    private String fromUser;

    public Notification(String drawingName, String fromUser) {
        this.drawingName = drawingName;
        this.fromUser = fromUser;
    }

    // Getters and setters

    public String getDrawingName() {
        return drawingName;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
}
