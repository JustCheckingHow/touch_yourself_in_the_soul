package com.example.wwydm.exploreyourself.exhibitOverview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wwydm.exploreyourself.R;

import java.io.ByteArrayOutputStream;

public class ExhibitsOverview extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_overview);

        RecyclerView.Adapter<ExhibitsOverviewAdapter.cardedImageHolder> imageAdapter = new ExhibitsOverviewAdapter(this);

        RecyclerView rv_images = (RecyclerView) findViewById(R.id.rv_images);
        rv_images.setAdapter(imageAdapter);
        rv_images.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showDetails(View v) {
        getWindow().setExitTransition(null);

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
}
