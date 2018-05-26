package com.example.wwydm.exploreyourself;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Random;

public class MainExploreActivity extends AppCompatActivity {
    ImageView iv_MainPhoto;
    Random random;

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

        random = new Random();
        iv_MainPhoto = findViewById(R.id.iv_MainPhoto);
        new BitmapDownloader().execute("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ random.nextInt(23000));
    }

    public void onButtonLike(View v) {
        animateOut();
        new BitmapDownloader().execute("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ random.nextInt(23000));
    }

    public void onButtonNotLike(View v) {
        new BitmapDownloader().execute("https://picsum.photos/200?id="+ random.nextInt(23000));
    }

    private void animateOut() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setDuration(500);

        iv_MainPhoto.startAnimation(fadeOut);
    }

    private void animateIn() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(500);

        iv_MainPhoto.startAnimation(fadeIn);
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