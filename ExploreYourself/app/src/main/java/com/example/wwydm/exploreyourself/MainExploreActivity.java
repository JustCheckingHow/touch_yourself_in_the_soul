package com.example.wwydm.exploreyourself;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wwydm.exploreyourself.serverapi.Exhibit;
import com.example.wwydm.exploreyourself.serverapi.ServerApi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.util.Vector;

public class MainExploreActivity extends AppCompatActivity implements ServerApi.ServerApiListener {
    ImageView iv_MainPhoto;
    private int currentID;
    private ServerApi sa;
    private  AlertDialog.Builder builder;

    private ProgressDialog pd;
    private Vector<Exhibit> toShow;


    private String currExtraTitle;
    private String currExtraCreator;


    static final int batchMaxCounter = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_explore);

        if (ContextCompat.checkSelfPermission(MainExploreActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainExploreActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    2);
        }


        FloatingActionButton myFab = findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onFabClick(v);
            }
        });

        sa = new ServerApi("192.168.1.106", this);
        sa.getExhibitsToShow(batchMaxCounter);

        pd = new ProgressDialog(MainExploreActivity.this);
        pd.setMessage("Fetching images");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
    }

    public void onFabClick(View v){
        sa.getExhibitsData(toShow.get(currentID));

        pd = new ProgressDialog(MainExploreActivity.this);
        pd.setMessage("Fetching extra info");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
    }

    public void onButtonLike(View v) {
        disableAllButtons();
        batchGuard(Exhibit.Choice.LIKE);
        animateOut();
        new BitmapDownloader().execute(toShow.get(currentID).getImageUrl());
        enableAllButtons();
    }

    public void onButtonNotLike(View v) {
        disableAllButtons();
        batchGuard(Exhibit.Choice.DISLIKE);
        animateOut();
        new BitmapDownloader().execute(toShow.get(currentID).getImageUrl());
        enableAllButtons();
    }

    public void onButtonNotDecided(View v) {
        disableAllButtons();
        batchGuard(Exhibit.Choice.NONE);
        animateOut();
        new BitmapDownloader().execute(toShow.get(currentID).getImageUrl());
        enableAllButtons();
    }

    private void disableAllButtons(){
        findViewById(R.id.button).setClickable(false);
        findViewById(R.id.button2).setClickable(false);
        findViewById(R.id.button3).setClickable(false);
    }

    private void enableAllButtons(){
        findViewById(R.id.button).setClickable(true);
        findViewById(R.id.button2).setClickable(true);
        findViewById(R.id.button3).setClickable(true);
    }

    private void batchGuard(Exhibit.Choice ch){
        if (currentID < batchMaxCounter - 1){
            toShow.get(currentID).setChoice(ch);
            currentID++;
        }
        else{
            toShow.get(currentID).setChoice(ch);
            Toast.makeText(MainExploreActivity.this,
                    "Exceeded batch number...Sending to https server", Toast.LENGTH_LONG).show();
            sa.postExhibitsRates(toShow);
            currentID = 0;
        }
        Log.e("APP INF", String.valueOf(currentID));
    }

    private void animateOut() {
        ValueAnimator fadeOut = ValueAnimator.ofFloat(1, 0);
        fadeOut.setDuration(500);
        fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iv_MainPhoto.setAlpha((float)valueAnimator.getAnimatedValue());
            }
        });
        fadeOut.start();
    }

    private void animateIn() {
        ValueAnimator fadeIn = ValueAnimator.ofFloat(0, 1);
        fadeIn.setDuration(500);
        fadeIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iv_MainPhoto.setAlpha((float)valueAnimator.getAnimatedValue());
            }
        });
        fadeIn.start();
    }

    @Override
    public void onGotExhibitsToShow(Vector<Exhibit> exhibits) {
        Log.d("APP", "Downloading data...");
        // TODO show got exhibits
        if (exhibits == null){
            Log.d("APP", "NULL");
        }
        for (int i = 0; i < exhibits.size(); i++) {
            exhibits.get(i).setImgUrl();
        }
        pd.setMessage("Images Loaded!");
        pd.dismiss();
        toShow = exhibits;

        iv_MainPhoto = findViewById(R.id.iv_MainPhoto);
        currentID =  0;
        new BitmapDownloader().execute(toShow.get(currentID).getImageUrl());
    }

    @Override
    public void onGotExhibitsData(String[] data) {
        currExtraCreator = data[1];
        currExtraTitle = data[0];

        String infoStringQuery = currExtraTitle;
        if (currExtraTitle == null){
            if (currExtraCreator == null){
                Log.d("APP INFO", "NULL AUTHOR");
                return;
            }
            else {
                infoStringQuery = currExtraCreator;
            }
        }
        else {
            Log.d("APP INFO", currExtraTitle);
        }
        Log.d("APP INFO", infoStringQuery);

        pd.dismiss();
        builder = new AlertDialog.Builder(MainExploreActivity.this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Artwork info: " + infoStringQuery)
                .setIcon(android.R.drawable.ic_menu_compass)
                .setCancelable(true);
        new JsonParser().execute(infoStringQuery, builder);
    }

    @Override
    public void onGotSuggestedExhibit(Exhibit e) {

    }

    private class BitmapDownloader extends AsyncTask {
        @Override
        protected Bitmap doInBackground(Object[] objects) {
            try {
                java.net.URL url = new java.net.URL((String)objects[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                currentID ++;
                if (currentID >= batchMaxCounter){
                    currentID = 0;
                    // exceeded batch number
                    sa.postExhibitsRates(toShow);
                }
                new BitmapDownloader().execute(toShow.get(currentID).getImageUrl());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            animateIn();
            iv_MainPhoto.setImageBitmap((Bitmap)result);
        }
    }
}