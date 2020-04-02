package com.lcj.debug.bitmapmatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Map;

public class ImageLoader {
    private static final int MSG_IMAGE_LOADED = 1;

    private static String TAG  = ImageLoader.class.getSimpleName();
    private static ImageLoader sImageLoader;

    private Context mContext;
    private Bitmap mDefaultBitmap;

    private Map<Integer, CallbackContext> mCallbacks = new ArrayMap();

    private int mID = 0;

    private ImageLoader(Context context) {
        mContext = context;
    }

    public static ImageLoader getInstance(Context context) {
        if (sImageLoader == null) {
            sImageLoader = new ImageLoader(context);
        }
        return sImageLoader;
    }

    public void getDefaultImage(LoadedCallback callback) {
        int id = getUniqeID();
        CallbackContext context = new CallbackContext(id, callback);
        MyThread thread = new MyThread("sdcard/png/cat2.jpg", context);
        mCallbacks.put(id, context);
        thread.start();
    }

    public void getImage(String file, LoadedCallback callback) {
        int id = getUniqeID();
        CallbackContext context = new CallbackContext(id, callback);
        MyThread thread = new MyThread(file, context);
        mCallbacks.put(id, context);
        thread.start();
    }

    private synchronized int getUniqeID () {
        return mID++;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_IMAGE_LOADED:
                    CallbackContext context = mCallbacks.get(msg.arg1);
                    context.callback.onLoaded(context.bitmap);
                    break;
                default:
                    break;
            }

        }
    };

    class MyThread extends Thread {
        private String mFileName;
        private CallbackContext mContext;

        MyThread(String file, CallbackContext context) {
            mFileName = file;
            this.mContext = context;
        }

        @Override
        public void run() {
            try {
                File file = new File(mFileName);
                ImageDecoder.Source source = ImageDecoder.createSource(file);
                mContext.bitmap = ImageDecoder.decodeBitmap(source);
                Message message = mHandler.obtainMessage(MSG_IMAGE_LOADED);
                message.arg1 = mContext.id;
                mHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CallbackContext {
        int id;
        Bitmap bitmap;
        LoadedCallback callback;

        CallbackContext(int id, LoadedCallback callback) {
            this.id = id;
            this.callback = callback;
        }
    }

    interface LoadedCallback {
        public void onLoaded(Bitmap bitmap);
    }
}
