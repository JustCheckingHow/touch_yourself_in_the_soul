package com.example.wwydm.exploreyourself.exhibitOverview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.wwydm.exploreyourself.MainExploreActivity;
import com.example.wwydm.exploreyourself.R;
import com.example.wwydm.exploreyourself.tripPropositions;


public class ExhibitDetails extends AppCompatActivity {
    ImageView main_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exhibit_card_details);
        getSupportActionBar().hide();
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                startActivity(new Intent(ExhibitDetails.this, MainExploreActivity.class));
                                return true;
                            case R.id.action_item2:
                                startActivity(new Intent(ExhibitDetails.this, ExhibitsOverview.class));
                                return true;
                            case R.id.action_item3:
                                startActivity(new Intent(ExhibitDetails.this, tripPropositions.class));
                                return true;
                        }
                        return false;
                    }
                });
//        bottomNavigationView.setSelectedItemId(R.id.action_item2);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        byte[] bytes = (byte[])b.get("bitmap");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        this.main_photo = findViewById(R.id.img_details);
        this.main_photo.setImageBitmap(bmp);
    }
}
