package com.luocj.demos.soundrecorder;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class SoundRecorderActivity extends Activity {
    public static final long KB = 1000L;
    public static final int BITRATE_AMR = 24 * (int) KB; // bits/sec
    public static final String FILE_EXTENSION_AMR = ".amr";
    private Button btn_RecordStart, btn_RecordStop;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private View.OnClickListener click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_RecordStart:
                    start();
                    break;
                case R.id.btn_RecordStop:
                    stop();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_recorder);

        btn_RecordStart = (Button) findViewById(R.id.btn_RecordStart);
        btn_RecordStop = (Button) findViewById(R.id.btn_RecordStop);

        btn_RecordStop.setEnabled(false);

        btn_RecordStart.setOnClickListener(click);
        btn_RecordStop.setOnClickListener(click);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermisionUtils.verifyStoragePermissions(this);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        String eventSource = "";
        String upInfo[] = eventSource.substring(eventSource.indexOf(':') + 1).split("/");
        int mUid = -1;
        int mPid = -1;
        try {
            mUid = Integer.valueOf(upInfo[0]);
            mPid = Integer.valueOf(upInfo[1]);
        } catch (Exception e) {
            mUid = -2;
            mPid = -2;
            Log.w("xm-av", "DeviceState update failed to format uid & pid");
        }

        Log.e("xm-av", "++++++++ 1 " + mUid + " / " + mPid);

        String pack[] = eventSource.split("/");
        try {
            mUid = Integer.valueOf(upInfo[0]);
            mPid = Integer.valueOf(upInfo[1]);
        } catch (Exception e) {
            mUid = -2;
            mPid = -2;
            Log.w("xm-av", "DeviceState update failed to format uid & pid");
        }
        Log.e("xm-av", "++++++++ 2 " + mUid + " / " + mPid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
    }

    private void test() {
        RecordingConfig recordingConfig = new RecordingConfig(MediaRecorder.OutputFormat.RAW_AMR,
                RecordingConfig.SAMPLINGRATE_AMR, RecordingConfig.SAMPLINGRATE_AMR_H,
                MediaRecorder.OutputFormat.AMR_NB, MediaRecorder.OutputFormat.AMR_WB,
                MediaRecorder.AudioEncoder.AMR_NB, MediaRecorder.AudioEncoder.AMR_WB,
                BITRATE_AMR, FILE_EXTENSION_AMR);
    }

    private void initRecorder(String mimetype, boolean highQuality) {
        RecordingConfig recordingConfig = new RecordingConfig(MediaRecorder.OutputFormat.AMR_WB,
                RecordingConfig.SAMPLINGRATE_AMR, RecordingConfig.SAMPLINGRATE_AMR_H,
                MediaRecorder.OutputFormat.AMR_NB, MediaRecorder.OutputFormat.AMR_WB,
                MediaRecorder.AudioEncoder.AMR_NB, MediaRecorder.AudioEncoder.AMR_WB,
                BITRATE_AMR, FILE_EXTENSION_AMR);
        mediaRecorder.setAudioSource(recordingConfig.mAudioSource);
        mediaRecorder.setAudioSamplingRate(highQuality ? recordingConfig.mSamplingRateH : recordingConfig.mSamplingRate);
        if (recordingConfig.mOutputFormat != RecordingConfig.INVALID_VALUE) {
            mediaRecorder.setOutputFormat(highQuality ? recordingConfig.mOutputFormatH : recordingConfig.mOutputFormat);
        }
        if (recordingConfig.mAudioEncorder != RecordingConfig.INVALID_VALUE) {
            mediaRecorder.setAudioEncoder(highQuality ? recordingConfig.mAudioEncorderH : recordingConfig.mAudioEncorder);
        }

        if (recordingConfig.mBitRate != RecordingConfig.INVALID_VALUE) {
            mediaRecorder.setAudioEncodingBitRate(recordingConfig.mBitRate);
        }
    }

    /**
     * 开始录音
     */
    protected void start() {
        try {
            File file = new File("/sdcard/mediarecorder.amr");
            if (file.exists()) {
                // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
                file.delete();
            }
            mediaRecorder = new MediaRecorder();
            initRecorder(null, true);
            // 设置录制音频文件输出文件路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());

            mediaRecorder.setOnErrorListener(new OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                    btn_RecordStart.setEnabled(true);
                    btn_RecordStop.setEnabled(false);
                    Toast.makeText(SoundRecorderActivity.this, "录音发生错误", 0).show();
                }
            });

            // 准备、开始
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            btn_RecordStart.setEnabled(false);
            btn_RecordStop.setEnabled(true);
            Toast.makeText(SoundRecorderActivity.this, "开始录音", 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音结束
     */
    protected void stop() {
        if (isRecording) {
            // 如果正在录音，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            btn_RecordStart.setEnabled(true);
            btn_RecordStop.setEnabled(false);
            Toast.makeText(SoundRecorderActivity.this, "录音结束", 0).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            // 如果正在录音，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }

}