package com.example.wwydm.exploreyourself;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

public class TripPropositions extends AppCompatActivity {
    LinearLayout imagesList;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_propositions);

        this.imagesList = findViewById(R.id.ll_imagesList);
        this.inflater = getLayoutInflater();

        LinearLayout others = findViewById(R.id.ll_userAdded);

        Random random = new Random();
        for (int i=0; i<3; i++)
           addExhibitToSuggested("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ random.nextInt(23000), imagesList);

        for (int i=0; i<3; i++)
            addExhibitToSuggested("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ random.nextInt(23000), others);
    }

    private void addExhibitToSuggested(String url, LinearLayout v) {
        new exhibitAdder().execute(url, v);
    }

    private class exhibitAdder extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                java.net.URL url = new java.net.URL((String)objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                ArrayList<Object> response = new ArrayList<>();
                response.add(BitmapFactory.decodeStream(input));
                response.add(objects[1]);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object obj) {
            ArrayList<Object> o = (ArrayList<Object>)obj;

            View view = inflater.inflate(R.layout.activity_mini, imagesList, false);
            ImageView img = view.findViewById(R.id.img);
            img.setImageBitmap((Bitmap) o.get(0));
            ((LinearLayout)o.get(1)).addView(view);
        }
    }
}
