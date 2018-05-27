package com.example.wwydm.exploreyourself.serverapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class ServerApi {
    private String serverAddr;

    public interface ServerApiListener {

        void onGotExhibitsToShow(Vector<Exhibit> exhibits);
        /**
         * [0] = title
         * [1] = creator
         * [2] = description
         * [3] = format
         * [4] = date
         * [5] = identifier
         */
        void onGotExhibitsData(String[] data);
        void onGotSuggestedExhibit(Exhibit e);
    }
    private ServerApiListener listener;

    public ServerApi(String serverAddress, ServerApiListener listener) {

        this.serverAddr = serverAddress;
        this.listener = listener;
    }

    /**
     * @param howManyExhibits number of requested pairs of photos
     * @return vector with pairs
     */
    public void getExhibitsToShow(int howManyExhibits) {

        get( "howMany=" + String.valueOf(howManyExhibits));
    }

    public void getExhibitsData(Exhibit e) {

        get( "options=" + e.getExId());
    }

    public void getSuggestedExhibit() {

        get("suggestion");
    }

    public void postExhibitsRates(Vector<Exhibit> exhibitsWithRates) {
        try {

            JSONObject json = new JSONObject();
            JSONArray a = new JSONArray();
            json.put("type", "exhibitsRates");

            for (Exhibit e : exhibitsWithRates) {
                JSONObject j = new JSONObject();
                j.put("id", e.getExId());
                j.put("rate", e.getChoice());
                a.put(j);
            }
            json.put("rates", a);
            new Thread(new HttpPost(this.serverAddr, json.toString())).start();
        } catch (Exception e) {
            System.err.print(e.getStackTrace().toString());
        }

    }

    private void get(String par) {
        try {
            new Thread(new HttpGet(this.serverAddr, par, listener)).start();
        } catch (Exception e) { }
    }
}


