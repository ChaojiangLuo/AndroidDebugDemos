package com.luocj.demos.audioeffect;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioEffectActivity extends Activity implements AudioEffect.OnControlStatusChangeListener, AudioEffect.OnEnableStatusChangeListener {

    private final String TAG = AudioEffectActivity.class.getSimpleName();

    private AudioEffect mAudioEffects;
    private Equalizer mEqualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_effect);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        MediaPlayer player = new MediaPlayer();
        player.getAudioSessionId();

        mAudioEffects.setControlStatusListener(this);
        mAudioEffects.setEnableStatusListener(this);

        mAudioEffects.setEnabled(true);
    }

    @Override
    public void onControlStatusChange(AudioEffect effect, boolean controlGranted) {

    }

    @Override
    public void onEnableStatusChange(AudioEffect effect, boolean enabled) {

    }

    private void setupEqualizerFxAndUI() {
        //调用MediaPlayer的代码就不在这里说明
        // 1，初始化一个均衡器默认使用 priority (0).
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true); //2，enable这个均衡器

        TextView eqTextView = new TextView(this);
        eqTextView.setText("Equalizer:");
        mLinearLayout.addView(eqTextView);

        short bands = mEqualizer.getNumberOfBands();
        //3，获取均衡器支持调节的频段 ，返回5，范围在60~14000HZ，具体查阅hw_sw,高通基线参考equalizer_band_presets_freq[NUM_EQ_BANDS] 路径hardware/qcom/audio/post_proc/equalizer.c

        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

        Log.d(TAG, "getBandLevelRange   min:" + minEQLevel + "  max:" + maxEQLevel);


        for (short i = 0; i < bands; i++) {
            final short band = i;
            //4这里主要是根据频bands以及每个band的区间，初始化可操作的seekbar，在事件响应函数中间操作设置参数到底层     frameworks/av/media/libeffects/lvm/wrapper/Bundle/EffectBundle.h
            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000) + " Hz");


            minDbTextView.setText((minEQLevel / 100) + " dB");
            maxDbTextView.setText((maxEQLevel / 100) + " dB");

            SeekBar bar = new SeekBar(this);

            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));  //seekbar响应函数中间的具体操作

                    Log.d(TAG, "seekbar operation: band: " + band + "  value:" + (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


        }
    }
}
