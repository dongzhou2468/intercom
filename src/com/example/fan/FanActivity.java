package com.example.fan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.record.R;

public class FanActivity extends Activity {

	private Button fanOn, fanOff;
	private String FAN_ON = "0000";
	private String FAN_OFF = "1111";
	private String serverIP = "192.168.1.100";
	private int serverPort = 4143;

	private boolean ON = false;
	private Thread fanControlThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fan);

		fanOn = (Button) findViewById(R.id.fanOn);
		fanOff = (Button) findViewById(R.id.fanOff);
		fanOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fanController(0);
			}
		});
		fanOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fanController(1);
			}
		});

	}

	public void fanController(final int on) {

		fanControlThread = new Thread(new Runnable() {
			public void run() {
				sendControl(on);
			}
		}, "Fan Control Thread");

		fanControlThread.start();
	}

	private void sendControl(int on) {

		Socket socket = null;
		try {
			socket = new Socket(serverIP, serverPort);
			//InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			if (on == 0)
				out.write(FAN_ON.getBytes());
			else if (on == 1)
				out.write(FAN_OFF.getBytes());
			
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
}
