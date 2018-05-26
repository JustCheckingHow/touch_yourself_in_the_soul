package com.example.wwydm.exploreyourself.serverapi;

public class Exhibit {
    public String getExId() {
        return exId;
    }

    private String exId;
    private Choice choice;

    public enum Choice {
        NONE,
        LIKE,
        DISLIKE
    }

    public Exhibit(String exhibitId) {

        this.exId = exhibitId;
    }

    public Exhibit(String exhibitId, Choice userChoice) {

        this.exId = exhibitId;
        this.choice = userChoice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public Choice getChoice() {
        return choice;
    }
}
