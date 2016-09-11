package com.example.transmission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.example.play.Play;

import android.util.Log;

public class Transmisson {

	//private static Thread sendingThread = null;
	private final static int PORT = 4142;
	private static String serverIP = "192.168.1.126";
	private static InetAddress IPAddress;
	private static DatagramSocket mSocket, stopSocket;

	private final static int PORT_FAN = 4143;
	
	public static void connect() {
		try {
			mSocket = new DatagramSocket(PORT);
			IPAddress = InetAddress.getByName(serverIP);
		} catch (IOException e) {
			Log.i("myLog", "DatagramSocket error......");
			e.printStackTrace();
		}
		if(mSocket != null){
			Log.i("myLog", "DatagramSocket OK......");
		}
	}

	public static void UDPSocketSend(byte[] data) {

		if (mSocket == null) {
			Log.i("myLog", "mSocket == null......");
			return;
		}
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				IPAddress, PORT);
		try {
			mSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i("myLog", "sending done......data.length: " + data.length);
	}

	public static void UDPSocketSendFan(byte[] data) {

		if (mSocket == null) {
			Log.i("myLog", "mSocket == null...UDPSocketSendFan");
			return;
		}
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				IPAddress, PORT_FAN);
		try {
			mSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i("myLog", "UDPSocketSendFan sending done......data.length: " + data.length);
	}
	
	public static byte[] UDPSocketRecv() {
		
		byte recvByte[] = new byte[2048];
		DatagramPacket recvPacket = new DatagramPacket(recvByte, recvByte.length);
		try {
			mSocket.receive(recvPacket);
		} catch (IOException e) {
			Log.i("myLog", "socket was closed? " + mSocket.isClosed());
			e.printStackTrace();
		}
		return recvByte;
	}
	
	public static void close() {
		
		if (mSocket == null) {
			return;
		}
		mSocket.close();
	}
}