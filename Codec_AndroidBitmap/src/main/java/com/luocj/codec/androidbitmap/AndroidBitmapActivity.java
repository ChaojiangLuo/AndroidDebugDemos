package com.luocj.codec.androidbitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AndroidBitmapActivity extends Activity {

    protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    private static final String TAG = AndroidBitmapActivity.class.getSimpleName();
    private String FILE_NAME = "/storage/emulated/0/DCIM/ScreenRecorder/Screenrecorder-2018-12-07-15-28-41-272.mp4";
    private String FILE_NAME2 = "file:///storage/emulated/0/DCIM/ScreenRecorder/Screenrecorder-2018-12-11-20-49-00-260.mp4";
    private String FILE_NAME3 = "file:///storage/emulated/0/DCIM/ScreenRecorder/Screenrecorder-2018-12-11-20-49-00-260.mp4";

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "ex 1 " + ex.toString());
        } catch (RuntimeException ex) {
            Log.e(TAG, "ex 2 " + ex.toString());
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) return null;

        return bitmap;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermisionUtils.verifyStoragePermissions(this);
        //updateImageView1();
        //updateImageView2();
        //updateImageView3();
        updateImageView4();
    }

    protected InputStream getStreamFromFile3(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        return new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
    }

    private void updateImageView5() {
        ImageView imageView5 = findViewById(R.id.imageview_02);
        Bitmap bitmap = null;
        try {
            bitmap = decode5(null);
        } catch (Exception e) {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 5 e " + e);
        }
        if (bitmap != null) {
            imageView5.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 5 failed " + FILE_NAME2);
        }
    }

    public Bitmap decode5(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap = null;

        InputStream imageStream = getStreamFromFile3(FILE_NAME3, null);
        if (imageStream == null) {
            Log.e(TAG, "getStreamFromFile fail");
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream = resetStream(imageStream, decodingInfo);
            byte[] data = new byte[32];
            try {
                imageStream.read(data, 0, 32);
                String dataString = new String(data);
                Log.e(TAG, "xm-gfx: getVideoThumbnailStream bitmap3 ok=" + dataString);
            } catch (Exception e) {
                Log.e(TAG, "xm-gfx: getVideoThumbnailStream bitmap3 fail " + e);
            }

            imageStream = resetStream(imageStream, decodingInfo);
            BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
            decodingOptions.inSampleSize = 2;
            decodingOptions.inPreferredConfig = Bitmap.Config.RGB_565;//ARGB_8888;
            Log.e(TAG, "decodedBitmap " + decodingOptions + " -- " + decodingOptions.toString());
            decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
        } finally {
            closeSilently(imageStream);
        }

        if (decodedBitmap == null) {
            Log.e(TAG, "decodedBitmap fail ");
        }

        return decodedBitmap;
    }

    private void updateImageView4() {
        ImageView imageView4 = findViewById(R.id.imageview_03);
        Bitmap bitmap = null;
        try {
            bitmap = decode(null);
        } catch (Exception e) {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 4 e " + e);
        }
        if (bitmap != null) {
            imageView4.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 4 failed " + FILE_NAME2);
        }
    }


    private InputStream getVideoThumbnailStream(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Bitmap bitmap = ThumbnailUtils
                    .createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            if (bitmap != null) {
                Log.e(TAG, "xm-gfx: getVideoThumbnailStream bitmap1 ok " + FILE_NAME2);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                Log.e(TAG, "xm-gfx: getVideoThumbnailStream bitmap2 ok " + FILE_NAME2 + "==" + bos.toString());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
                return inputStream;
            }
        }
        return null;
    }

    private boolean isVideoFileUri(String uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        Log.e(TAG, "xm-gfx: isVideoFileUri extension " + extension + " uri " + uri);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Log.e(TAG, "xm-gfx: isVideoFileUri extension " + extension + " mimeType " + mimeType + " uri " + uri);
        return mimeType != null && mimeType.startsWith("video/");
    }

    protected InputStream getStreamFromFile(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        if (isVideoFileUri(imageUri)) {
            return getVideoThumbnailStream(filePath);
        } else {
            BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
            return new ContentLengthInputStream(imageStream, (int) new File(filePath).length());
        }
    }

    public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap = null;

        InputStream imageStream = getStreamFromFile(FILE_NAME2, null);
        if (imageStream == null) {
            Log.e(TAG, "getStreamFromFile fail");
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream = resetStream(imageStream, decodingInfo);
            byte[] data = new byte[32];
            try {
                imageStream.read(data, 0, 32);
                String dataString = new String(data);
                Log.e(TAG, "xm-gfx: getVideoThumbnailStream bitmap3 ok=" + dataString);
            } catch (Exception e) {
                Log.e(TAG, "xm-gfx: getVideoThumbnailStream bitmap3 fail " + e);
            }

            imageStream = resetStream(imageStream, decodingInfo);
            BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
            decodingOptions.inSampleSize = 2;
            decodingOptions.inPreferredConfig = Bitmap.Config.RGB_565;//ARGB_8888;
            Log.e(TAG, "decodedBitmap " + decodingOptions + " -- " + decodingOptions.toString());
            decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
        } finally {
            closeSilently(imageStream);
        }

        if (decodedBitmap == null) {
            Log.e(TAG, "decodedBitmap fail ");
        }

        return decodedBitmap;
    }

    protected InputStream resetStream(InputStream imageStream, ImageDecodingInfo decodingInfo) throws IOException {
        if (imageStream.markSupported()) {
            try {
                imageStream.reset();
                return imageStream;
            } catch (IOException ignored) {
            }
        }
        closeSilently(imageStream);
        return getStreamFromFile(FILE_NAME2, null);
    }

    private void updateImageView1() {
        ImageView imageView1 = findViewById(R.id.imageview_01);
        Bitmap bitmap = ThumbnailUtils
                .createVideoThumbnail(FILE_NAME, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        if (bitmap != null) {
            imageView1.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 1 failed " + FILE_NAME);
        }
    }

    private void updateImageView2() {
        ImageView imageView2 = findViewById(R.id.imageview_02);
        Bitmap bitmap = createVideoThumbnail(FILE_NAME, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        if (bitmap != null) {
            imageView2.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 2 failed " + FILE_NAME);
        }
    }

    private void updateImageView3() {
        ImageView imageView3 = findViewById(R.id.imageview_03);
        Bitmap bitmap = null;
        try {
            bitmap = createByDataSource(FILE_NAME2);
        } catch (Exception e) {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 3 e " + e);
        }
        if (bitmap != null) {
            imageView3.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "xm-gfx: createVideoThumbnail 3 failed " + FILE_NAME2);
        }
    }

    public Bitmap createByDataSource(String path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try (FileInputStream is = new FileInputStream(path)) {
            FileDescriptor fd = is.getFD();
            retriever.setDataSource(fd, 0, 0x7ffffffffffffffL);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (FileNotFoundException fileEx) {
            throw new IllegalArgumentException();
        } catch (IOException ioEx) {
            throw new IllegalArgumentException();
        }
        return bitmap;
    }

    public enum Scheme {
        HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * Defines scheme of incoming URI
         *
         * @param uri URI for scheme detection
         * @return Scheme of incoming URI
         */
        public static Scheme ofUri(String uri) {
            if (uri != null) {
                for (Scheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.startsWith(uriPrefix);
        }

        /** Appends scheme to incoming path */
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /** Removed scheme part ("scheme://") from incoming URI */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }
}
