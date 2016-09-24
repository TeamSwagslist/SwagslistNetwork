package com.aidancbrady.swagslist.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread
{
	public Socket socket;
	
	public BufferedReader reader;
	
	public PrintWriter writer;
	
	public boolean disconnected;
	
	public Connection(Socket s)
	{
		socket = s;
	}
	
	@Override
	public void run()
	{
		ServerCore.instance().getConnections().add(this);
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			String reading = "";
			
			while((reading = reader.readLine()) != null && !disconnected)
			{
				String msg = reading.trim();
			}
			
			System.out.println("Closing connection with " + socket.getInetAddress());
			
			writer.flush();
			close();
		} catch(Throwable t) {
			System.out.println("Connection thread error");
			t.printStackTrace();
		}
		
		ServerCore.instance().getConnections().remove(this);
	}
	
	public void close()
	{
		disconnected = true;
		
		try {
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
