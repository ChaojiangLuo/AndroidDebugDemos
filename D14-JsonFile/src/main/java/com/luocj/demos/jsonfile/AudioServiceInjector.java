package com.luocj.demos.jsonfile;

import android.content.Context;
import android.media.AudioStatusHandler;
import android.os.Bundle;

public class AudioServiceInjector {
    public static void handleModeChanged(Context context, int pid, int mode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AudioStatusHandler.KEY_CALLER, AudioStatusHandler.Caller.MODE);
        bundle.putInt(AudioStatusHandler.KEY_PID, pid);
        bundle.putInt(AudioStatusHandler.KEY_STATE, mode);
        handleAudioStatusChanged(context, bundle);
    }

    public static void handleSpeakerChanged(Context context, int pid, boolean speakeron) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AudioStatusHandler.KEY_CALLER, AudioStatusHandler.Caller.SPEAKER);
        bundle.putInt(AudioStatusHandler.KEY_PID, pid);
        bundle.putInt(AudioStatusHandler.KEY_STATE, speakeron ? 1 : 0);
        handleAudioStatusChanged(context, bundle);
    }

    public static void handleAudioStatusChanged(Context context, Bundle bundle) {
        AudioStatusHandler statusHandler = AudioStatusHandler.getInstance(context);
        statusHandler.handleAudioStatusChanged(bundle);
    }
}
