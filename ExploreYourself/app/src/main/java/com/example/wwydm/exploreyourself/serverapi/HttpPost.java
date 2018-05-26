package com.example.wwydm.exploreyourself.serverapi;

import android.util.Log;

import org.json.JSONException;
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

public class HttpPost implements Runnable {
    private URL url;
    private String urlParameters;
    private HttpURLConnection connection;
    protected StringBuffer response;
    private String message;

    public HttpPost(String message)
    {
        this.message = message;
    }

    @Override
    public void run()
    {
        try{
            url = new URL("http://"
                    + "192.168.43.144"
                    + ":"
                    + "80");
//                     + //Globals.getServerApiDestination());
        }catch(MalformedURLException ex) {
            Log.e("Http:", "new URL:", ex);
        }
        try{
            urlParameters = "" + URLEncoder.encode(message, "UTF-8");
        }catch(UnsupportedEncodingException ex){
            Log.e("Http:", "urlParams", ex);
        }

        try{
            connection = (HttpURLConnection) url.openConnection();
        }catch(java.io.IOException ex){
            Log.e("Http:", "openConnection", ex);
        }

        try{
            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
        }catch(Exception ex)
        {
            Log.e("Http", "connectionSetup", ex);
        }

        //Send request
        try{
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();
        }
        catch(ConnectException ce)
        {
            Log.d("HTTP", "Connection error - can't connect to address:" + url.toString());
            //TODO show widget with connection error.
            return;
        }
        catch(Exception ex)
        {
            Log.e("Http", "sendRequest", ex);
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

        //Encode Response with JSON
        try {
            JSONObject json = new JSONObject(response.toString());
            String msg = json.getString("msg");
            Log.d("JSON decoded: ", msg);

        }catch(JSONException ex){
            Log.e("JSON", "parseHTTPResponse", ex);
        }
    }
}

