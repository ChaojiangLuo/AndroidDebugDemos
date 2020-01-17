package com.luocj.demos.imageview;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageViewDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_demo);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ImageView imageView = findViewById(R.id.image_view);
                imageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_background));
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
