package com.example.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.record.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MusicActivity extends Activity{

	private Button lastSong, nextSong, play, stop;
	private TextView playingSong;
	
	private String serverIP = "192.168.1.131";
	private int serverPort = 4243;
	
	private int songID = 1;
	private int sun = 5;
	private final static String STOP = "stop";
	
	//private static boolean playing = false;
	private Thread musicControlThread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		
		lastSong = (Button) findViewById(R.id.lastSong);
		nextSong = (Button) findViewById(R.id.nextSong);
		play = (Button) findViewById(R.id.play);
		stop = (Button) findViewById(R.id.stop);
		
		playingSong = (TextView) findViewById(R.id.music);
		playingSong.setText(getSongName(songID));
		
		lastSong.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(songID == 1)
					songID = 5;
				else
					songID--;
				String str = getSongName(songID);
				playingSong.setText(str);
			}
		});
		nextSong.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(songID == 5)
					songID = 1;
				else
					songID++;
				String str = getSongName(songID);
				playingSong.setText(str);
			}
		});
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
					String ctrl = String.valueOf(songID);
					musicController(ctrl);
			}
		});
		stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
					musicController(STOP);
			}
		});
	}
	
	public void musicController(final String ctrl) {

		//should use Handler!
		musicControlThread = new Thread(new Runnable() {
			public void run() {
				sendControl(ctrl.getBytes());
			}
		}, "Fan Control Thread");

		musicControlThread.start();
	}
	
	private void sendControl(byte[] data) {

		Socket socket = null;
		try {
			socket = new Socket(serverIP, serverPort);
			//InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(data);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getSongName(int id) {
		
		JSONObject songObject;
		String songName = "";
		try {
			InputStream is = getResources().getAssets().open("song_edison.json");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer songs = new StringBuffer();
			String str = "";
			while ((str = br.readLine()) != null) {
				songs.append(str);
			}
			str = songs.toString();
			JSONObject songJson = new JSONObject(str);
			JSONArray songArray = songJson.getJSONArray("songs");
			songObject = songArray.getJSONObject(id - 1);
			songName = songObject.getString("song");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return songName;
	}
}
