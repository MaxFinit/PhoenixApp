package com.maxfin.phoenixapp.managers;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class RecordManager {

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String mFileName;

    public RecordManager() {
        mFileName = Environment.getExternalStorageDirectory() + "/record.3gpp";
    }

    public void recordStart() {


            releaseRecorder();

            File outFile = new File(mFileName);
            if (outFile.exists()) {
                outFile.delete();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(mFileName);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

    }

    public void recordStop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    public void playStart() {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(mFileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void playStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }


    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}