package com.lcj.debug.bitmapmatrix;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;

public class BitmapMatrixActivity extends AppCompatActivity {

    private static final String TAG = BitmapMatrixActivity.class.getSimpleName();

    private MyImageView mImageViews[] = new  MyImageView[15];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermisionUtils.verifyStoragePermissions(this);

        setContentView(R.layout.activity_bitmap_matrix);

        mImageViews[0] = findViewById(R.id.imageview_01);
        mImageViews[0].setIndex(1);
        Matrix matrix0 = new Matrix();
        mImageViews[0].setMatrix(matrix0);
        mImageViews[0].setLabel("Normal");
        Log.e(TAG, "matrix0 " + matrix0.toString());

        mImageViews[1] = findViewById(R.id.imageview_02);
        mImageViews[1].setIndex(2);
        Matrix matrix1 = new Matrix();
        float[] pts = {
                0.8f, 0.0f, 0.0f,
                0.0f, 0.8f, 0.0f,
                0.0f, 0.0f, 1.0f};
        matrix1.setValues(pts);
        mImageViews[1].setMatrix(matrix1);
        mImageViews[1].setLabel("DownScale 0.8");
        Log.e(TAG, "matrix1 " + matrix1.toString());

        mImageViews[2] = findViewById(R.id.imageview_03);
        mImageViews[2].setIndex(3);
        Matrix matrix2 = new Matrix();
        pts = new float[]{1.0f, 0.0f, 50.0f,
                0.0f, 1.0f, 50.0f,
                0.0f, 0.0f, 1.0f};
        matrix2.setValues(pts);
        mImageViews[2].setMatrix(matrix2);
        mImageViews[2].setLabel("Translate 50x50");
        Log.e(TAG, "matrix2 " + matrix2.toString());

        mImageViews[3] = findViewById(R.id.imageview_04);
        mImageViews[3].setIndex(4);
        Matrix matrix3 = new Matrix();
        pts = new float[] {
                0.8f, 0.0f, 50.0f,
                0.0f, 0.8f, 50.0f,
                0.0f, 0.0f, 1.0f};
        matrix3.setValues(pts);
        mImageViews[3].setMatrix(matrix3);
        mImageViews[3].setLabel("DownScale&Translate");
        Log.e(TAG, "matrix3 " + matrix3.toString());

        mImageViews[4] = findViewById(R.id.imageview_05);
        mImageViews[4].setIndex(5);
        Matrix matrix4 = new Matrix();
        pts = new float[] {
                1.0f, 0.5f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f};
        matrix4.setValues(pts);
        mImageViews[4].setMatrix(matrix4);
        mImageViews[4].setLabel("SkewX 0.5");
        Log.e(TAG, "matrix4 " + matrix4.toString());

        mImageViews[5] = findViewById(R.id.imageview_06);
        mImageViews[5].setIndex(6);
        pts = new float[] {
                1.0f, 0.0f, 0.0f,
                0.5f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f};
        Matrix matrix5 = new Matrix();
        matrix5.setValues(pts);
        mImageViews[5].setMatrix(matrix5);
        mImageViews[5].setLabel("SkewY 0.5");
        Log.e(TAG, "matrix5 " + matrix5.toString());

        mImageViews[6] = findViewById(R.id.imageview_07);
        mImageViews[6].setIndex(7);
        pts = new float[] {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.001f, 0.0f, 1.0f};
        Matrix matrix6 = new Matrix();
        matrix6.setValues(pts);
        mImageViews[6].setMatrix(matrix6);
        mImageViews[6].setLabel("perspectiveX 0.001");
        Log.e(TAG, "matrix6 " + matrix6.toString());

        mImageViews[7] = findViewById(R.id.imageview_08);
        mImageViews[7].setIndex(8);
        pts = new float[] {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.001f, 1.0f};
        Matrix matrix7 = new Matrix();
        matrix7.setValues(pts);
        mImageViews[7].setMatrix(matrix7);
        mImageViews[7].setLabel("perspectiveY 0.001");
        Log.e(TAG, "matrix7 " + matrix7.toString());

        mImageViews[8] = findViewById(R.id.imageview_09);
        mImageViews[8].setIndex(9);
        pts = new float[] {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.001f, 0.001f, 1.0f};
        Matrix matrix8 = new Matrix();
        matrix8.setValues(pts);
        mImageViews[8].setMatrix(matrix8);
        mImageViews[8].setLabel("perspectiveXY");
        Log.e(TAG, "matrix8 " + matrix8.toString());

        mImageViews[9] = findViewById(R.id.imageview_10);
        mImageViews[9].setIndex(10);
        pts = new float[] {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.001f, 0.001f, 2.0f};
        Matrix matrix9 = new Matrix();
        matrix9.setValues(pts);
        mImageViews[9].setMatrix(matrix9);
        mImageViews[9].setLabel("perspectiveScale 2");
        Log.e(TAG, "matrix9 " + matrix9.toString());

        mImageViews[10] = findViewById(R.id.imageview_11);
        mImageViews[10].setIndex(11);
        Matrix matrix10 = new Matrix();
        matrix10.setRotate(30);
        mImageViews[10].setMatrix(matrix10);
        mImageViews[10].setLabel("Rotate 30");
        Log.e(TAG, "matrix10 " + matrix10.toString());


        mImageViews[11] = findViewById(R.id.imageview_12);
        mImageViews[11].setIndex(12);
        Matrix matrix11 = new Matrix();
        matrix11.setRotate(-30);
        mImageViews[11].setMatrix(matrix11);
        mImageViews[11].setLabel("Rotate -30");
        Log.e(TAG, "matrix11 " + matrix11.toString());


        mImageViews[12] = findViewById(R.id.imageview_13);
        mImageViews[12].setIndex(13);
        Matrix matrix12 = new Matrix();
        matrix12.setRotate(30, 200, 200);
        mImageViews[12].setMatrix(matrix12);
        mImageViews[12].setLabel("Rotate 30 at[200x200]");
        Log.e(TAG, "matrix12 " + matrix12.toString());


        mImageViews[13] = findViewById(R.id.imageview_14);
        mImageViews[13].setIndex(14);
        Matrix matrix13 = new Matrix();
        matrix13.setSinCos(0.3f, 1.0f);
        mImageViews[13].setMatrix(matrix13);
        mImageViews[13].setLabel("SinCos[0.3,1.0]");
        Log.e(TAG, "matrix13 " + matrix13.toString());

        mImageViews[14] = findViewById(R.id.imageview_15);
        mImageViews[14].setIndex(15);
        Matrix matrix14 = new Matrix();
        matrix14.setSinCos(0.3f, 1.0f, 100, 100);
        mImageViews[14].setMatrix(matrix14);
        mImageViews[14].setLabel("SinCos[0.3,1.0] at[100,100]");
        Log.e(TAG, "matrix14 " + matrix14.toString());
    }
}
