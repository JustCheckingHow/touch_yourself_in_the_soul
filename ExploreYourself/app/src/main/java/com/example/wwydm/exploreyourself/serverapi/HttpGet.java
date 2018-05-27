package com.example.wwydm.exploreyourself.serverapi;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

public class HttpGet implements Runnable {
    private URL url;
    private String urlParameters;
    private HttpURLConnection connection;
    protected StringBuffer response;
    private String message;
    private ServerApi.ServerApiListener listener;
    private String serverAddress;

    public HttpGet(String serverAddress, String message, ServerApi.ServerApiListener listener)
    {
        this.serverAddress = serverAddress;
        this.message = message;
        this.listener = listener;
    }

    @Override
    public void run()
    {
        try{
            url = new URL("http://"
                    + this.serverAddress
                    + ":"
                    + "80?" + this.message);
//                     + //Globals.getServerApiDestination());
        }catch(MalformedURLException ex) {
            Log.e("Http:", "new URL:", ex);
        }

        try{
            connection = (HttpURLConnection) url.openConnection();
        }catch(java.io.IOException ex){
            Log.e("Http:", "openConnection", ex);
        }

        try{
            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Content-Type", "application/json");
        }catch(Exception ex)
        {
            Log.e("Http", "connectionSetup", ex);
        }

        //Get Response
        try {
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
        } catch(Exception ex)
        {
            Log.e("Http", "getResponse", ex);
        }

        // Parse result

        if (this.message.contains("howMany")) {

            Vector<Exhibit> toReturn = new Vector<>();
            try {
                JSONObject jObject = new JSONObject(response.toString());
                JSONArray jArray = jObject.getJSONArray("exhibitsIds");

                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject o = jArray.getJSONObject(i);
                    toReturn.add(new Exhibit(o.getString("id")));
                }

                listener.onGotExhibitsToShow(toReturn);
            } catch (Exception e) {

                listener.onGotExhibitsToShow(toReturn);
            }
        } else if (this.message.contains("options")) {

            try {
                JSONObject jObject = new JSONObject(response.toString());
                String[] data = new String[6];
                data[0] = jObject.getString("title");
                data[1] = jObject.getString("creator");
                data[2] = jObject.getString("description");
                data[3] = jObject.getString("format");
                data[4] = jObject.getString("date");
                data[5] = jObject.getString("identifier");
                listener.onGotExhibitsData(data);
            } catch (Exception e) {
                Log.e("INFO", "Bad JSON in response");
            }
        } else if (this.message.contains("suggestion")) {

            Vector<Exhibit> toReturn = new Vector<>();
            try {
                JSONObject jObject = new JSONObject(response.toString());
                JSONArray jArray = jObject.getJSONArray("exhibitsIds");

                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject o = jArray.getJSONObject(i);
                    toReturn.add(new Exhibit(o.getString("id")));
                }

                listener.onGotSuggestedExhibit(toReturn);
            } catch (Exception e) {

                listener.onGotSuggestedExhibit(toReturn);
            }
        }

    }
}


