package com.example.play;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.example.transmission.Transmisson;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

public class Play {

	private static Thread playingThread = null;
	private static int finalRawTag;
	static AudioTrack audioTrack;
	private static int frequency = 44100;
	private static boolean isPlaying = false;
	
	public static void audioTrackPlay() {
		
		int bufferSizeInBytes = AudioTrack.getMinBufferSize(frequency,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequency,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				2 * bufferSizeInBytes, AudioTrack.MODE_STREAM);
		audioTrack.play();
	}
	
	private static void audioTrackClose() {
		audioTrack.stop();
		audioTrack.release();
		audioTrack = null;
	}
	
	public static void replay(int a) {

		finalRawTag = a;
		playingThread = new Thread(new Runnable() {
			public void run() {
				loadRawAndPlay();
			}
		}, "Playing Thread");

		playingThread.start();
	}

	public static void loadRawAndPlay() {

		String filepath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String fileName = filepath + "/AudioRecorder/record_temp--0.raw";
		File file = null;

		byte[] out = new byte[1024];
		InputStream is = null;
		int len = 0;
		int count = 0;
		try {
			while(finalRawTag / 24 >= count) {
				
				count++;
				file = new File(fileName);
				is = new FileInputStream(file);
				while ((len = is.read(out)) != -1) {
					audioTrack.write(out, 0, len);
					Log.i("myLog", "audioTrack.write=" + len);
				}
				
				is.close();
				//file.delete();
				fileName = filepath + "/AudioRecorder/record_temp--" + 24 * count + ".raw";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		audioTrackClose();
	}

	public static void readStreamAndPlay() {
		int count = 0;
		byte recvbyte[] = new byte[2048];
		while(isPlaying){
			recvbyte = Transmisson.UDPSocketRecv();
			audioTrack.write(recvbyte, 0, recvbyte.length);
			Log.i("myLog", "recv times: " + ++count);
		}
		audioTrackClose();
	}
	
	public static void setPlaying(boolean status) {
		isPlaying = status;
		//synchronized issue! will cause NPE at audioTrack.write()
		/*if (!isPlaying) {
			audioTrack.stop();
			audioTrack.release();
			audioTrack = null;
		}*/
	}
	
}