package com.example.wwydm.exploreyourself.serverapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class ServerApi {
    private String serverAddr;

    public ServerApi(String serverAddress) {

        serverAddr = serverAddress;
    }

    /**
     * @param howManyExhibits number of requested pairs of photos
     * @return vector with pairs
     */
    public Vector<Exhibit> getExhibitsToShow(int howManyExhibits) {

        String response = get( "howMany=" + String.valueOf(howManyExhibits) );
        Vector<Exhibit> toReturn = new Vector<>();

        try {
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("exhibitsIds");

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject o = jArray.getJSONObject(i);
                toReturn.add(new Exhibit(o.getString("id")));
            }
            return toReturn;
        } catch (Exception e) {

            return toReturn;
        }
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

            post(json.toString());
        } catch (Exception e) {

        }

    }

    private String get(String par) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(serverAddr + "?" + par);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {

            return "";
        }
        return result.toString();
    }

    private void post(String msg) {

        try {
            String url = serverAddr;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(msg);
            wr.flush();
            wr.close();
        } catch (Exception e) {

        }
    }
}
