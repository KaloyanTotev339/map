package com.example.listview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;



public class ImageActivity extends AppCompatActivity {
    private final String TAG = "ImageView";
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        img = (ImageView) findViewById(R.id.imageView_id);

        Intent i = getIntent();
        String roomNum = i.getStringExtra("roomNumber");
        Log.d(TAG, "room number " + roomNum);
        int imageRescource = getResources().getIdentifier("@drawable/r"+roomNum,null, this.getPackageName());
        img.setImageResource(imageRescource);
    }
}