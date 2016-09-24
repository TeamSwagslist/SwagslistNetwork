package com.aidancbrady.swagslist.network;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import com.aidancbrady.swagslist.Account;
import com.aidancbrady.swagslist.EventEntry;

public class ServerCore 
{
	private static final ServerCore INSTANCE = new ServerCore();
	
	public static final int SERVER_PORT = 29000;
	
	private ServerSocket socket;
	
	private boolean serverRunning = false;
	
	private Set<Connection> connections = new HashSet<Connection>();
	
	private Set<Account> accounts = new HashSet<Account>();
	
	private Set<EventEntry> events = new HashSet<EventEntry>();
	
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
	
	public Account lookup(String username)
	{
		for(Account acct : accounts)
		{
			if(acct.getUsername().equals(username))
			{
				return acct;
			}
		}
		
		return null;
	}
	
	public void addAccount(Account account)
	{
		accounts.add(account);
	}
	
	public void addEvent(EventEntry entry)
	{
		events.add(entry);
	}
	
	public boolean containsEvent(String eventName)
	{
		for(Iterator<EventEntry> iter = events.iterator(); iter.hasNext();)
		{
			EventEntry entry = iter.next();
			
			if(entry.getName().equals(eventName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void removeEvent(String eventName)
	{
		for(Iterator<EventEntry> iter = events.iterator(); iter.hasNext();)
		{
			EventEntry entry = iter.next();
			
			if(entry.getName().equals(eventName))
			{
				iter.remove();
				break;
			}
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
	
	public Set<Connection> getConnections()
	{
		return connections;
	}
	
	public Set<EventEntry> getEvents()
	{
		return events;
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
