package com.aidancbrady.swagslist.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import com.aidancbrady.swagslist.Account;
import com.aidancbrady.swagslist.EventEntry;

public class Connection extends Thread
{
	public static final String SPLITTER = ",";
	public static final String SPLITTER_2 = ";";
	
	private static final Set<Character> ALLOWED_CHARS = new HashSet<Character>();
	private static final int MAX_USERNAME_LENGTH = 24;
	
	static {
		ALLOWED_CHARS.add('-');
		ALLOWED_CHARS.add('_');
		ALLOWED_CHARS.add('.');
	}
	
	public Socket socket;
	
	public BufferedReader reader;
	
	public PrintWriter writer;
	
	public boolean disconnected;
	
	public Account account;
	
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
				String[] msg = reading.trim().split(SPLITTER);
				
				if(msg[0].equals("AUTH") && msg.length == 3)
				{
					Account acct = ServerCore.instance().lookup(msg[1]);
					
					if(acct != null && acct.checkPassword(msg[2]))
					{
						account = acct;
						writer.println(compileMsg(ResponseState.ACCEPT));
					}
					else {
						writer.println(compileMsg(ResponseState.REJECT, "Bad credentials"));
					}
				}
				else if(msg[0].equals("REGISTER") && msg.length == 4)
				{
					if(ServerCore.instance().lookup(msg[2]) != null)
					{
						writer.println(compileMsg(ResponseState.REJECT, "Account already exists"));
					}
					else if(!isValidUsername(msg[2]))
					{
						writer.println(compileMsg(ResponseState.REJECT, "Invalid username"));
					}
					
					Account acct = new Account(msg[1], msg[2]);
					
					if(acct.initPassword(msg[3]))
					{
						ServerCore.instance().addAccount(acct);
						writer.println(compileMsg(ResponseState.ACCEPT));
					}
					else {
						writer.println(compileMsg(ResponseState.REJECT, "Failed to create account"));
					}
				}
				else if(msg[0].equals("NEWENTRY"))
				{
					if(account == null)
					{
						writer.println(compileMsg(ResponseState.REJECT, "Not authenticated"));
					}
					else {
						EventEntry newEntry = EventEntry.createEntry(msg, 1);
						
						if(newEntry == null)
						{
							writer.println(compileMsg(ResponseState.REJECT, "Unable to create event"));
						}
						else if(ServerCore.instance().containsEvent(newEntry.getName()))
						{
							writer.println(compileMsg(ResponseState.REJECT, "Event already exists"));
						}
						else {
							ServerCore.instance().addEvent(newEntry);
							writer.println(compileMsg(ResponseState.ACCEPT));
						}
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
						
						if(!ServerCore.instance().containsEvent(origName))
						{
							writer.println(compileMsg(ResponseState.REJECT, "Event does not exist"));
						}
						else {
							EventEntry newEntry = EventEntry.createEntry(msg, 2);
							
							if(newEntry == null)
							{
								writer.println(compileMsg(ResponseState.REJECT, "Unable to create event"));
							}
							else {
								ServerCore.instance().addEvent(newEntry);
								writer.println(compileMsg(ResponseState.ACCEPT));
							}
						}
					}
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
		if(s.length() > MAX_USERNAME_LENGTH) return false;
		
		for(char c : s.toCharArray())
		{
			if(!ALLOWED_CHARS.contains(c) && !Character.isLetterOrDigit(c))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static String compileMsg(ResponseState state, Object... strings)
	{
		StringBuilder str = new StringBuilder();
		
		str.append(state.name() + (strings.length > 0 ? SPLITTER : ""));
		
		for(int i = 0; i < strings.length; i++)
		{
			str.append(strings[i]);
			
			if(i < strings.length-1)
			{
				str.append(SPLITTER);
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
