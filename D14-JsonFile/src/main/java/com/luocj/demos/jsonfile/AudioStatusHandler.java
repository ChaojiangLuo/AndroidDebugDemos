package android.media;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;

import java.util.Map;

/**
 * @hide
 */
public class AudioStatusHandler {
    public static final String KEY_TYPE = "type";
    public static final String KEY_PID = "pid";
    public static final String KEY_STATE = "state";

    private static final String TAG = AudioStatusHandler.class.getSimpleName();
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    private static final int MESSAGE_SEND_MOTIFICATION = 10001;
    private static final int MESSAGE_CANCEL_AUDIOSTATUS = 10002;

    private static final long DELAY = 1 * 60 * 1000L;
    private static AudioStatusHandler sAudioStatusHandler;

    private Context mContext;
    private Map<Type, AudioState> mAudioState = new ArrayMap<>();

    // should run in main loop
    private Handler mHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SEND_MOTIFICATION:
                    sendAudioStatusNotification();
                    break;
                case MESSAGE_CANCEL_AUDIOSTATUS:
                    cancelAudioStatusNotification();
                    break;
                default:
                    break;
            }
        }
    };

    private AudioStatusHandler(Context context) {
        mContext = context;
    }

    public static AudioStatusHandler getInstance(Context context) {
        if (sAudioStatusHandler == null) {
            synchronized (AudioStatusHandler.class) {
                if (sAudioStatusHandler == null) {
                    sAudioStatusHandler = new AudioStatusHandler(context);
                }
            }
        }
        return sAudioStatusHandler;
    }

    public void handleAudioStatusChanged(Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return;
        }

        Type type = (Type) bundle.getSerializable(KEY_TYPE);
        AudioState audioState = mAudioState.get(type);

        if (audioState != null && !audioState.isChanged(bundle)) {
            return;
        }

        if (audioState == null) {
            audioState = new AudioState(type);
            mAudioState.put(type, audioState);
        }

        if (!audioState.update(bundle)) {
            if (DEBUG) {
                Log.w(TAG, "handleAudioStatusChanged no update " + type);
            }
            return;
        }

        if (DEBUG) {
            Log.e(TAG, "handleAudioStatusChanged type " + audioState.type + " pid "
                    + audioState.pid + " state " + audioState.state);
        }

        onAudioStatusChanged();
    }

    // We just care communication state now
    private void onAudioStatusChanged() {
        // 1.check communication state changed
        AudioState communicationState = mAudioState.get(Type.MODE);
        if (communicationState == null) {
            return;
        }

        // 2.cancel pending message
        if (mHandle.hasMessages(MESSAGE_SEND_MOTIFICATION)) {
            mHandle.removeMessages(MESSAGE_SEND_MOTIFICATION);
        }

        // 3.cancel notify when mode not in communication, should run in main loop
        if (communicationState.state != AudioManager.MODE_IN_COMMUNICATION) {
            mHandle.sendMessage(mHandle.obtainMessage(MESSAGE_CANCEL_AUDIOSTATUS));
            return;
        }

        // 4.compute the dalay time
        long currentTime = SystemClock.elapsedRealtime();
        if (communicationState.startTime == 0) {
            communicationState.startTime = currentTime;
        }

        long delay = DELAY - (currentTime - communicationState.startTime);
        if (delay < 0) {
            delay = 0;
        }

        // 5. send notify with delay, shold run in main loop
        mHandle.sendMessageDelayed(mHandle.obtainMessage(MESSAGE_SEND_MOTIFICATION), delay);
    }

    private void cancelAudioStatusNotification() {
        NotificationManager manager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(AudioStatusNotify.NOTIFY_ID);

        if (DEBUG) {
            Log.e(TAG, "cancelAudioStatusNotification id " + AudioStatusNotify.NOTIFY_ID);
        }
    }

    private void sendAudioStatusNotification() {
        // 1.check communication state changed
        AudioState communicationState = mAudioState.get(Type.MODE);
        if (communicationState == null) {
            return;
        }

        if (communicationState.state != AudioManager.MODE_IN_COMMUNICATION) {
            Log.w(TAG, "sendAudioStatusNotification not communication mode " + communicationState.state);
            return;
        }

        AudioState speakerState = mAudioState.get(Type.SPEAKER);
        boolean isSpeakerOn = speakerState != null && speakerState.state != 0;

        if (DEBUG) {
            Log.e(TAG, "sendAudioStatusNotification type " + communicationState.type + " pid "
                    + communicationState.pid + " mode " + communicationState.state + " speakerOn " + isSpeakerOn);
        }

        AudioStatusNotify.sendAudioStatusNotification(mContext, communicationState.pid, isSpeakerOn);
    }

    public enum Type {MODE, SPEAKER}

    class AudioState {
        Type type;
        int pid;
        int state;
        long startTime;

        public AudioState(Type type) {
            this.type = type;
            this.pid = 0;
            this.state = 0;
            this.startTime = 0;
        }

        public boolean isChanged(Bundle bundle) {
            if (bundle == null || bundle.isEmpty()) {
                return false;
            }

            if (this.type != (Type) bundle.getSerializable(KEY_TYPE)) {
                return false;
            }

            int inPid = bundle.getInt(KEY_PID, -1);
            int inState = bundle.getInt(KEY_STATE, 0);
            if (inPid != this.pid || inState != this.state) {
                return true;
            }
            return false;
        }

        public boolean update(Bundle bundle) {
            if (bundle == null || bundle.isEmpty()) {
                return false;
            }

            if (this.type != (Type) bundle.getSerializable(KEY_TYPE)) {
                return false;
            }

            this.pid = bundle.getInt(KEY_PID, 0);
            this.state = bundle.getInt(KEY_STATE, 0);
            if (this.startTime == 0) {
                this.startTime = SystemClock.elapsedRealtime();
            }
            if (this.state == 0) {
                startTime = 0;
            }
            return true;
        }
    }
}