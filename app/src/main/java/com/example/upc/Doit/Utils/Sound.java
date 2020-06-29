package com.example.upc.Doit.Utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.preference.PreferenceManager;



public class Sound {
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private MediaPlayer mNextPlayer;

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mMediaPlayer = mNextPlayer;
            setNextMediaPlayer(getMusicid());
        }
    };

    public Sound(Context context) {
        mContext = context;
        build(getMusicid());
    }



    private void build(int id) {
        mMediaPlayer = MediaPlayer.create(mContext, id);
        setNextMediaPlayer(id);
    }

    private void setNextMediaPlayer(int id) {
        mNextPlayer = MediaPlayer.create(mContext, id);
        mMediaPlayer.setNextMediaPlayer(mNextPlayer);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    public void play() {
        if (isSoundOn()) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resume() {
        if (isSoundOn()) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }
    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            release();
            build(getMusicid());
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

            mNextPlayer.stop();
            mNextPlayer.release();
            mNextPlayer = null;
        }
    }

    public void reset(){
        mMediaPlayer.reset();
        build(getMusicid());
    }

    private boolean isSoundOn() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean("pref_key_tick_sound", true);
    }

    private int getMusicid(){
        return (int)SPUtils.get(mContext, "music_id",1);
    }
}
