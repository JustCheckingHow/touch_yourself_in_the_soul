package com.example.wwydm.exploreyourself.serverapi;

public class Exhibit {
    static final String predefined_URL = "http://cyfrowe.mnw.art.pl/Content/";

    public String getExId() {
        // exID is in form "/" and
        return exId.split("/")[0];
    }
    public String getFullExId(){
        return this.exId;
    }

    private String exId;
    private Choice choice;
    private  String imgUrl;

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

    public void getImageUrl(){
        this.imgUrl = predefined_URL + this.exId + ".jpg";
    }

    public Choice getChoice() {
        return choice;
    }
}
