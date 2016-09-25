package com.aidancbrady.swagslist.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

import com.aidancbrady.swagslist.Account;
import com.aidancbrady.swagslist.EventEntry;
import com.aidancbrady.swagslist.SharedData;

public class ActiveConnection extends Thread
{	
	public Socket socket;
	
	public BufferedReader reader;
	
	public PrintWriter writer;
	
	public boolean disconnected;
	
	public Account account;
	
	public ActiveConnection(Socket s)
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
			System.out.println("Connection initialized with " + socket.getInetAddress());
			String reading = "";
			
			while((reading = reader.readLine()) != null && !disconnected)
			{
				System.out.println("Received msg " + reading.trim());
				String[] msg = reading.trim().split(SharedData.SPLITTER);
				
				if(msg[0].equals("AUTH") && msg.length == 3)
				{
					Account acct = SQLHandler.lookup(msg[1]);
					
					if(acct != null && acct.checkPassword(msg[2]))
					{
						account = acct;
						writer.println(compileMsg(ResponseState.ACCEPT));
					}
					else {
						writer.println(compileMsg(ResponseState.REJECT, "Bad credentials"));
					}
				}
				else if(msg[0].equals("REGISTER") && msg.length == 3)
				{
					if(SQLHandler.lookup(msg[1]) != null)
					{
						writer.println(compileMsg(ResponseState.REJECT, "Account already exists"));
					}
					else if(!isValidUsername(msg[1]))
					{
						writer.println(compileMsg(ResponseState.REJECT, "Invalid username"));
					}
					else {
						Account acct = new Account(msg[1]);
						
						if(acct.initPassword(msg[2]))
						{
							SQLHandler.addAccount(acct);
							writer.println(compileMsg(ResponseState.ACCEPT));
						}
						else {
							writer.println(compileMsg(ResponseState.REJECT, "Failed to create account"));
						}
					}
				}
				else if(msg[0].equals("NEWENTRY"))
				{
					EventEntry newEntry = EventEntry.createFromCSV(msg, 1);
					
					if(newEntry == null)
					{
						writer.println(compileMsg(ResponseState.REJECT, "Unable to create event"));
					}
					else if(SQLHandler.containsEvent(newEntry.getName()))
					{
						writer.println(compileMsg(ResponseState.REJECT, "Event already exists"));
					}
					else {
						SQLHandler.addEvent(newEntry);
						writer.println(compileMsg(ResponseState.ACCEPT));
					}
				}
				else if(msg[0].equals("EDITENTRY") && msg.length > 2)
				{
					if(account == null)
					{
						writer.println(compileMsg(ResponseState.REJECT, "Not authenticated"));
					}
					else {
						String origName = msg[1];
						
						if(!SQLHandler.containsEvent(origName))
						{
							writer.println(compileMsg(ResponseState.REJECT, "Event does not exist"));
						}
						else {
							EventEntry newEntry = EventEntry.createFromCSV(msg, 2);
							
							if(newEntry == null)
							{
								writer.println(compileMsg(ResponseState.REJECT, "Unable to create event"));
							}
							else if(!account.getUsername().equals(newEntry.getOwnerUsername()))
							{
								writer.println(compileMsg(ResponseState.REJECT, "Invalid event owner"));
							}
							else {
								SQLHandler.addEvent(newEntry);
								writer.println(compileMsg(ResponseState.ACCEPT));
							}
						}
					}
				}
				else if(msg[0].equals("LISTENTRIES"))
				{
					Set<EventEntry> entries = SQLHandler.getEvents();
					StringBuilder builder = new StringBuilder();
					builder.append(ResponseState.ACCEPT + SharedData.PRIME_SPLITTER + entries.size());
					if(entries.size() > 0) builder.append(SharedData.PRIME_SPLITTER);
					
					for(Iterator<EventEntry> iter = entries.iterator(); iter.hasNext();)
					{
						builder.append(iter.next().toCSV());
						if(iter.hasNext()) builder.append(SharedData.PRIME_SPLITTER);
					}
					
					writer.println(builder.toString());
				}
				else {
					writer.println(compileMsg(ResponseState.REJECT, "Unknown command"));
				}
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
	
	public boolean isValidUsername(String s)
	{
		if(s.length() > SharedData.MAX_USERNAME_LENGTH) return false;
		
		for(char c : s.toCharArray())
		{
			if(!SharedData.ALLOWED_CHARS.contains(c) && !Character.isLetterOrDigit(c))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static String compileMsg(ResponseState state, Object... strings)
	{
		return compileMsg(state, SharedData.SPLITTER, strings);
	}
	
	public static String compileMsg(ResponseState state, String splitter, Object... strings)
	{
		StringBuilder str = new StringBuilder();
		
		str.append(state.name() + (strings.length > 0 ? splitter : ""));
		
		for(int i = 0; i < strings.length; i++)
		{
			str.append(strings[i]);
			
			if(i < strings.length-1)
			{
				str.append(splitter);
			}
		}
		
		return str.toString();
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
	
	public static enum ResponseState
	{
		ACCEPT,
		REJECT;
	}
}
