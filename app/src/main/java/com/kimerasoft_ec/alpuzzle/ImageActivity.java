package com.kimerasoft_ec.alpuzzle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    private ImageView ivImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        if (MainActivity.currentImage != null)
            ivImage.setImageBitmap(MainActivity.currentImage);
        else
            finish();
    }
}
