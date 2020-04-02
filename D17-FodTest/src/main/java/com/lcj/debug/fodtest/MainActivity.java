package com.lcj.debug.fodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    WindowManager.LayoutParams mLayoutParams;

    private Button mButtomHBM;
    private Button mButtomAnim;
    private Button mButtomTouch;
    private Button mButtomIcon;


    private View mHBMView;
    private View mAnimView;
    private View mTouchView;
    private View mIconView;

    private WindowManager mWindowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        mButtomHBM = findViewById(R.id.bottom_hbm);
        mButtomAnim = findViewById(R.id.bottom_anim);
        mButtomTouch = findViewById(R.id.bottom_touch);
        mButtomIcon = findViewById(R.id.bottom_icon);

        mButtomHBM.setOnClickListener(this);
        mButtomAnim.setOnClickListener(this);
        mButtomTouch.setOnClickListener(this);
        mButtomIcon.setOnClickListener(this);

        PermisionUtils.verifyPermissions(this);
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            Log.e("luocj", "pack " + getPackageName());
            startActivityForResult(intent, 1234);
        }
    }

    @Override
    public void onClick(View view) {
        showFodDialog(view.getId());
    }

    private void showFodDialog(int id) {
        switch (id) {
            case R.id.bottom_hbm:
                if(mHBMView != null) {
                    mWindowManager.removeView(mHBMView);
                    mHBMView = null;
                    break;
                }
                mHBMView = showHBMView();
                break;
            case R.id.bottom_anim:
                if(mAnimView != null) {
                    mWindowManager.removeView(mAnimView);
                    mAnimView = null;
                    break;
                }
                mAnimView = showAnimView();
                break;
            case R.id.bottom_touch:
                if(mTouchView != null) {
                    mWindowManager.removeView(mTouchView);
                    mTouchView = null;
                    break;
                }
                mTouchView = showTouchView();
                break;
            case R.id.bottom_icon:
                if(mIconView != null) {
                    mWindowManager.removeView(mIconView);
                    mIconView = null;
                    break;
                }
                mIconView = showIconView();
                break;
            default:
                break;
        }
    }

    private View showHBMView() {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.drawable.timg);

            mLayoutParams = new WindowManager.LayoutParams(256, 256,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.RGBA_8888);
            mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            //mLayoutParams.alpha = 0.0f;
            mLayoutParams.setTitle("overlay");

            mLayoutParams.x = 400;
            mLayoutParams.y = 400;

            mWindowManager.addView(imageView, mLayoutParams);

            return imageView;
        }

    private View showAnimView() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.timg2);

        mLayoutParams = new WindowManager.LayoutParams(256, 256,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888);
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        //mLayoutParams.alpha = 0.0f;
        mLayoutParams.setTitle("anim");

        mLayoutParams.x = 400;
        mLayoutParams.y = 800;

        mWindowManager.addView(imageView, mLayoutParams);

        return imageView;
    }

    private View showTouchView() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.timg2);

        mLayoutParams = new WindowManager.LayoutParams(256, 256,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888);
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        //mLayoutParams.alpha = 0.0f;
        mLayoutParams.setTitle("touch");

        mLayoutParams.x = 400;
        mLayoutParams.y = 1200;

        mWindowManager.addView(imageView, mLayoutParams);

        return imageView;
    }

    private View showIconView() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.timg2);

        mLayoutParams = new WindowManager.LayoutParams(256, 256,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888);
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        //mLayoutParams.alpha = 0.0f;
        mLayoutParams.setTitle("icon");

        mLayoutParams.x = 400;
        mLayoutParams.y = 1600;

        mWindowManager.addView(imageView, mLayoutParams);

        return imageView;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mHBMView != null) {
            mWindowManager.removeView(mHBMView);
            mHBMView = null;
        }

        if(mAnimView != null) {
            mWindowManager.removeView(mAnimView);
            mAnimView = null;
        }

        if(mTouchView != null) {
            mWindowManager.removeView(mTouchView);
            mTouchView = null;
        }

        if(mIconView != null) {
            mWindowManager.removeView(mIconView);
            mIconView = null;
        }
    }
}
