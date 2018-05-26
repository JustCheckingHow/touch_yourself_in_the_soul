package com.example.wwydm.exploreyourself;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wwydm.exploreyourself.serverapi.Exhibit;
import com.example.wwydm.exploreyourself.serverapi.ServerApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Random;
import java.util.Vector;

public class MainExploreActivity extends AppCompatActivity {
    ImageView iv_MainPhoto;
    Random random;
    private int batchCounter;
    Vector<Exhibit> batchAssessment;
    private int currentID;
    ServerApi sa;

    static final int batchMaxCounter = 3;
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

        sa = new ServerApi("http://192.168.0.201:80");
        batchCounter = 0;
        batchAssessment = new Vector<>(batchMaxCounter);

        random = new Random();
        iv_MainPhoto = findViewById(R.id.iv_MainPhoto);
        currentID =  random.nextInt(23000);
        new BitmapDownloader().execute("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ currentID);
    }

    public void onButtonLike(View v) {
        batchGuard(Exhibit.Choice.LIKE);
        animateOut();
        currentID =  random.nextInt(23000);
        new BitmapDownloader().execute("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ currentID);
    }

    public void onButtonNotLike(View v) {
        batchGuard(Exhibit.Choice.DISLIKE);
        animateOut();
        currentID =  random.nextInt(23000);
        new BitmapDownloader().execute("https://picsum.photos/200?id="+ currentID);
    }

    public void onButtonNotDecided(View v) {
        batchGuard(Exhibit.Choice.NONE);
        animateOut();
        currentID =  random.nextInt(23000);
        new BitmapDownloader().execute("https://picsum.photos/200?id="+ currentID);
    }

    private void batchGuard(Exhibit.Choice ch){
        if (batchCounter < batchMaxCounter-1){
            batchAssessment.add(new Exhibit(String.valueOf(currentID), ch));
            batchCounter ++;
        }
        else{
            Toast.makeText(MainExploreActivity.this,
                    "Exceeded batch number...Sending to https server", Toast.LENGTH_LONG).show();
            sa.postExhibitsRates(batchAssessment);
        }
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