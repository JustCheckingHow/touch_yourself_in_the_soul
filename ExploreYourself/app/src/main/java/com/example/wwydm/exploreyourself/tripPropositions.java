package com.example.wwydm.exploreyourself;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wwydm.exploreyourself.exhibitOverview.ExhibitDetails;
import com.example.wwydm.exploreyourself.exhibitOverview.ExhibitsOverview;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Random;

public class tripPropositions extends AppCompatActivity {
    LinearLayout imagesList;
    LayoutInflater inflater;
    private BottomSheetBehavior mBottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_propositions);

        this.imagesList = findViewById(R.id.ll_imagesList);
        this.inflater = getLayoutInflater();
//        View bottomSheet = findViewById(R.id.bottom_sheet);
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //        LinearLayout others = findViewById(R.id.ll_userAdded);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                startActivity(new Intent(tripPropositions.this, MainExploreActivity.class));
                                return true;
                            case R.id.action_item2:
                                startActivity(new Intent(tripPropositions.this, ExhibitsOverview.class));
                                return true;
                            case R.id.action_item3:
                                return true;
                        }
                        return false;
                    }
                });
        bottomNavigationView.setSelectedItemId(R.id.action_item3);

        Random random = new Random();
        for (int i=0; i<3; i++)
           addExhibitToSuggested("https://picsum.photos/200/"+(random.nextInt(4)*100+100)+"/?id="+ random.nextInt(23000), imagesList);
    }

    public void onTripPlanItemClick(View v) {
        for(int i=1; i< imagesList.getChildCount(); i++) {
            imagesList.getChildAt(i).setBackgroundColor(Color.WHITE);
            ((TextView)imagesList.getChildAt(i).findViewById(R.id.tv_title)).setTextColor(Color.BLACK);
            ((TextView)imagesList.getChildAt(i).findViewById(R.id.tv_surname)).setTextColor(Color.GRAY);
        }
        v.setBackgroundColor(getColor(R.color.colorAccentBackground));
        ((TextView)v.findViewById(R.id.tv_title)).setTextColor(Color.WHITE);
        ((TextView)v.findViewById(R.id.tv_surname)).setTextColor(Color.WHITE);
        showBottomScrollView();
    }

    private void addExhibitToSuggested(String url, LinearLayout v) {
        new exhibitAdder().execute(url, v);
    }

    private void showBottomScrollView(){
        BottomScrollView bsv = new BottomScrollView();
        bsv.show(getSupportFragmentManager(), "Bottom sheet");
    }

    public void showRouteMenu(View view){
        startActivity(new Intent(tripPropositions.this, NavigationUtility.class));
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

            View view = inflater.inflate(R.layout.activity_trip_mini_item, imagesList, false);
            ImageView img = view.findViewById(R.id.img);
            img.setImageBitmap((Bitmap) o.get(0));
            ((LinearLayout)o.get(1)).addView(view);
        }
    }
}
