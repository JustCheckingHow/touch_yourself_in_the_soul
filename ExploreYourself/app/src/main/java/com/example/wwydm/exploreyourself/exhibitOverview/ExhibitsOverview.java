package com.example.wwydm.exploreyourself.exhibitOverview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wwydm.exploreyourself.JsonParser;
import com.example.wwydm.exploreyourself.MainExploreActivity;
import com.example.wwydm.exploreyourself.R;
import com.example.wwydm.exploreyourself.serverapi.ServerApi;
import com.example.wwydm.exploreyourself.tripPropositions;
import com.example.wwydm.exploreyourself.serverapi.Exhibit;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class ExhibitsOverview extends AppCompatActivity implements ServerApi.ServerApiListener {

    private TextView mTextMessage;
    private Vector<Exhibit> localSuggestions;
//    private RecyclerView.Adapter<ExhibitsOverviewAdapter.cardedImageHolder> imageAdapter;
    ExhibitsOverviewAdapter imageAdapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_overview);

        // get suggested views
        ServerApi sa = new ServerApi("192.168.1.106", this);
        sa.getSuggestedExhibit(3); // request 3 photos
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // wait for pictures
        pd = new ProgressDialog(ExhibitsOverview.this);
        pd.setMessage("Fetching images");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                startActivity(new Intent(ExhibitsOverview.this, MainExploreActivity.class));
                                return true;
                            case R.id.action_item2:
                                return true;
                            case R.id.action_item3:
                                startActivity(new Intent(ExhibitsOverview.this, tripPropositions.class));
                                return true;
                        }
                        return false;
                    }
                });

        bottomNavigationView.setSelectedItemId(R.id.action_item2);
    }

    public void showDetails(View v) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        BitmapDrawable bmp_drawable = (BitmapDrawable)((ImageView)v).getDrawable();
        Bitmap bmp = bmp_drawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Intent intent = new Intent(ExhibitsOverview.this, ExhibitDetails.class);
        intent.putExtra("bitmap", bytes.toByteArray());

        Pair<View, String> pair1 = Pair.create((View)findViewById(R.id.ll_header), "image_header");
        Pair<View, String> pair2 = Pair.create((View)findViewById(R.id.img_details), "image_details");
        Pair<View, String> pair3 = Pair.create((View)findViewById(R.id.tv_description), "image_desc");

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ExhibitsOverview.this, pair1, pair2, pair3);

        startActivity(intent, options.toBundle());
    }

    @Override
    public void onGotExhibitsToShow(Vector<Exhibit> exhibits) {

    }

    @Override
    public void onGotExhibitsData(String[] data) {
        String currExtraCreator = data[1];
        String currExtraTitle = data[0];

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
        new JsonParser().execute(infoStringQuery, findViewById(R.id.tv_description));
    }

    @Override
    public void onGotSuggestedExhibit(Vector<Exhibit> e) {
        Log.d("APP INFO", "NAFSFASsf");
        localSuggestions = e;

        if (localSuggestions != null){
            String[] urlsToPass = new String[e.size()];
            for (int i =0 ;i < e.size(); i++){
                localSuggestions.get(i).setImgUrl();
                urlsToPass[i] = localSuggestions.get(i).getImageUrl();
            }
            Log.d("APP INFO", String.valueOf(e.size()));
            imageAdapter = new ExhibitsOverviewAdapter(this, urlsToPass);
        }
        else{
            Log.d("APP INFO", "NULL!!!");
        }

        RecyclerView rv_images = (RecyclerView) findViewById(R.id.rv_images);
        rv_images.setAdapter(imageAdapter);
        rv_images.setLayoutManager(new LinearLayoutManager(this));
        pd.dismiss();
    }
}
