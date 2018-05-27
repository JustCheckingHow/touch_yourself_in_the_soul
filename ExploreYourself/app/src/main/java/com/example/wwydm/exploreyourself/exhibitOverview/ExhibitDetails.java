package com.example.wwydm.exploreyourself.exhibitOverview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.wwydm.exploreyourself.R;


public class ExhibitDetails extends AppCompatActivity {
    ImageView main_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exhibit_card_details);
        getSupportActionBar().hide();
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        byte[] bytes = (byte[])b.get("bitmap");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        this.main_photo = findViewById(R.id.img_details);
        this.main_photo.setImageBitmap(bmp);
    }
}
