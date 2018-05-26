package com.example.wwydm.exploreyourself.serverapi;

public class Exhibit {
    public String getExId() {
        return exId;
    }

    private String exId;
    private Choice choice;

    public enum Choice {
        LIKE,
        DISLIKE,
        NONE
    }

    public Exhibit(String exhibitId) {

        exId = exhibitId;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public Choice getChoice() {
        return choice;
    }
}
