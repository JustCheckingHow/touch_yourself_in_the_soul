package com.example.wwydm.exploreyourself;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by Jakub on 26/05/2018.
 */

public class JsonParser extends AsyncTask<Object, Void, ArrayList<Object>>
{
    @Override
    protected ArrayList<Object> doInBackground(Object... objects)
    {
        //objects is by order: query (string) and v (View)
        String str="https://en.wikipedia.org/api/rest_v1/page/summary/" + (String) objects[0];
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(str);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            ArrayList<Object> ao = new ArrayList<>();
            ao.add(new JSONObject(stringBuffer.toString()));
            ao.add(objects[1]);
            return ao;
        }
        catch(Exception ex)
        {
            ArrayList<Object> ao2 = new ArrayList<>();
            ao2.add(objects[0]);
            ao2.add(objects[1]);
            Log.e("App", "AsyncTask error", ex);
            return ao2;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Object> response)
    {
        if(response != null)
        {
            try {
                String extract;
                Object r = response.get(0);
                try{
                    JSONObject tj = (JSONObject) r;
                    extract = tj.getString("extract");

                }
                catch (ClassCastException e){
                    extract =(String) r;
                }
                Log.e("APP", extract);
                Object build = response.get(1);
                android.support.v7.app.AlertDialog.Builder ab = (android.support.v7.app.AlertDialog.Builder) build;
                ((android.support.v7.app.AlertDialog.Builder) ab).setMessage(extract).show();
            } catch (JSONException ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}

