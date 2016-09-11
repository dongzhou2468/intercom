package com.example.bell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.example.record.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BellService extends Service {

	private int PORT = 4144;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("myLog", "bell service created...");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				bellListenTcp();
			}
		}).start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	
	private void bellListenTcp() {
		
		ServerSocket sSocket = null;
		byte[] recvBuffer = new byte[4];
		int recvSize = 0;
		String recvString;
		
		try {
			sSocket = new ServerSocket(PORT);
			while (true) {
				Socket cSocket = sSocket.accept();
				SocketAddress cAddress = cSocket.getRemoteSocketAddress();
				Log.i("myLog", "receiving from: " + cAddress);
				InputStream is = cSocket.getInputStream();
				//OutputStream os = cSocket.getOutputStream();
				
				while ((recvSize = is.read(recvBuffer)) != -1) {
					recvString = new String(recvBuffer);
					Log.i("myLog", "receive " + recvString);
					
					if (recvString.equals("1024")) {
						bellCallResponse();
					}
				}
				cSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//NPE?
//			try {
//				sSocket.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	private void bellCallResponse() {

		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(intent);
	}
}
