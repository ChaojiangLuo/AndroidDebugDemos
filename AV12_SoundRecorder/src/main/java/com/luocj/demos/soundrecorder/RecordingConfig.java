package com.luocj.demos.soundrecorder;

import android.media.MediaRecorder;

public class RecordingConfig {
    public static final int INVALID_VALUE = -1;
    public static final int SAMPLINGRATE_DEFAULT = 44100;
    public static final int SAMPLINGRATE_3GPP_H = 22050;
    public static final int SAMPLINGRATE_AMR = 8000;
    public static final int SAMPLINGRATE_AMR_H = 16000;

    int mAudioSource;
    int mAudioType;
    int mSamplingRate;
    int mSamplingRateH;
    int mOutputFormat = INVALID_VALUE;
    int mOutputFormatH = INVALID_VALUE;
    int mAudioEncorder = INVALID_VALUE;
    int mAudioEncorderH = INVALID_VALUE;
    int mBitRate = INVALID_VALUE;
    String mExtension;

    public RecordingConfig(int audioType, int samplingRate, int samplingRateH,
                           int outputFormat, int outputFormatH, int audioEncorder, int audioEncorderH, int bitRate,
                           String extension) {
        mAudioSource = MediaRecorder.AudioSource.MIC;
        mAudioType = audioType;
        mSamplingRate = samplingRate;
        mSamplingRateH = samplingRateH;
        mOutputFormat = outputFormat;
        mOutputFormatH = outputFormatH;
        mAudioEncorder = audioEncorder;
        mAudioEncorderH = audioEncorderH;
        mBitRate = bitRate;
        mExtension = extension;
    }
}
