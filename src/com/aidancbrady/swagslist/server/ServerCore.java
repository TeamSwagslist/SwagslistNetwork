package com.aidancbrady.swagslist.server;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ServerCore 
{
	private static final ServerCore INSTANCE = new ServerCore();
	
	public static final int SERVER_PORT = 29000;
	
	private ServerSocket socket;
	
	private boolean serverRunning = false;
	
	private Set<ActiveConnection> connections = new HashSet<ActiveConnection>();
	
	private void init()
	{
		try {
			socket = new ServerSocket(SERVER_PORT);
			new ConnectionHandler().start();
			new ServerTimer().start();
			
			serverRunning = true;
			
			System.out.println("Server initiated.");
			
			Scanner scan = new Scanner(System.in);
			
			while(scan.hasNext())
			{
				String s = scan.nextLine();
				
				if(s.equals("stop") || s.equals("quit"))
				{
					System.out.println("Shutting down");
					scan.close();
					quit();
				}
			}
		} catch(Exception e) {
			System.out.println("Unable to start server");
			e.printStackTrace();
		}
	}
	
	public boolean isRunning()
	{
		return serverRunning;
	}
	
	public ServerSocket getSocket()
	{
		return socket;
	}
	
	public Set<ActiveConnection> getConnections()
	{
		return connections;
	}
	
	private void quit()
	{
		serverRunning = false;
		
		try {
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	
	public static void main(String[] args)
	{
		instance().init();
	}
	
	public static ServerCore instance()
	{
		return INSTANCE;
	}
}
