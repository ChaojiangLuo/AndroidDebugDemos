package com.lcj.debug.bitmapmatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MyImageView extends AppCompatImageView implements ImageLoader.LoadedCallback {
    private static final String TAG  = MyImageView.class.getSimpleName();
    private static final int[] COLORS  = new int[] {Color.GREEN, Color.BLUE, Color.RED};

    private Context mContext;

    private Bitmap mBitmap = null;
    private Paint mBitmapPaint;

    private int mIndex = 0;
    private Matrix mMatrix;
    private String mLabel;

    public MyImageView(Context context) {
        this(context, null);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mMatrix = new Matrix();

        Log.e(TAG, "new");

        mBitmapPaint = new Paint();
    }

    @Override
    public void onLoaded(Bitmap bitmap) {
        if(bitmap != null && bitmap != mBitmap) {
            Log.e(TAG, "onLoaded index " + mIndex + " " + bitmap.getWidth() + " " + bitmap.getHeight());
            mBitmap = bitmap;
            invalidate();
            requestLayout();
        }
    }

    public int getImageWidth() {
        return mBitmap != null ? mBitmap.getWidth() : 0;
    }

    public int getImageHeight() {
        return mBitmap != null ? mBitmap.getHeight() : 0;
    }
    public void setIndex(int index) {
        mIndex = index;
    }

    public  void setMatrix(Matrix matrix) {
        mMatrix = matrix;
    }

    public void setImage(String file) {
        ImageLoader.getInstance(mContext).getImage(file, this);
    }

    public void setLabel(String lebal) {
        mLabel = lebal;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        canvas.drawColor(Color.BLUE);
        if(mBitmap != null) {
            Log.e(this.getClass().getSimpleName(), "drawBitmap bitmap " + mBitmap.getWidth() + " x " + mBitmap.getHeight());
            if (mIndex == 16) {
                Rect src = new Rect();
                Rect dst = new Rect();
                src.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                dst.set(0, 0, getWidth(), getHeight());
                canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            } else {
                canvas.drawBitmap(mBitmap, mMatrix, mBitmapPaint);
            }
            mBitmapPaint.setTextSize(20f);
            mBitmapPaint.setColor(Color.RED);
            canvas.drawText(mMatrix.toShortString(), 10, getHeight() - 50, mBitmapPaint);
            if(!mLabel.isEmpty()) {
                mBitmapPaint.setTextSize(30f);
                canvas.drawText(mLabel, 20, getHeight() - 100, mBitmapPaint);
            }
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStrokeWidth(10);
            canvas.drawLine(0, 0, getWidth(), 0, mBitmapPaint);
            canvas.drawLine(0, 0, 0, getHeight(), mBitmapPaint);
            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), mBitmapPaint);
            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), mBitmapPaint);
        } else {
            ImageLoader.getInstance(mContext).getDefaultImage(this);
        }
    }

}
