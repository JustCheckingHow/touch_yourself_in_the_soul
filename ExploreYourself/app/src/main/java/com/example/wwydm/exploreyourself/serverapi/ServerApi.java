package com.example.wwydm.exploreyourself.serverapi;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
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
            new Post().execute(json.toString());
        } catch (Exception e) {
            System.err.print(e.getStackTrace().toString());
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

    public class Post extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] msga) {
                try {
                    String msg = (String) msga[0];
                    Log.d("INFO", "HEllo");
                    String url = serverAddr;
                    URL obj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                    conn.setRequestProperty("Content-Length", Integer.toString(msg.getBytes().length));

                    //add request header
                    conn.setRequestMethod("POST");

                    // Send post request
                    conn.setDoOutput(true);

//            BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
//
//            BufferedWriter wr = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(msg);
//                    wr.write(msg);
                    wr.flush();
                    wr.close();
                    conn.connect();

                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    String etrace = sw.toString();
                    Log.d("ERR", etrace);
                }
                return null;
        }
    }
    private void post(String msg) {

        try {
            Log.d("INFO", "HEllo");
            String url = serverAddr;
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Content-Length", Integer.toString(msg.getBytes().length));

            //add request header
            conn.setRequestMethod("POST");

            // Send post request
            conn.setDoOutput(true);

//            BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
//
//            BufferedWriter wr = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(msg);
//            wr.write(msg);
            wr.flush();
            wr.close();
            conn.connect();

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            String etrace = sw.toString();
            Log.d("ERR", etrace);
        }
    }
}
