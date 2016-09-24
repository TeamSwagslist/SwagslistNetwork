package com.aidancbrady.swagslist.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aidancbrady.swagslist.EventEntry;
import com.aidancbrady.swagslist.SessionData;
import com.aidancbrady.swagslist.SharedData;

public class ClientNetworkHandler 
{
	public static List<String> sendMessages(String... messages)
	{
		try {
			List<String> responses = new ArrayList<String>();
			Socket socket = new Socket(SharedData.SERVER_IP, SharedData.SERVER_PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			
			for(String message : messages)
			{
				writer.println(message);
			}
			
			String reading = "";
			
			while((reading = reader.readLine()) != null)
			{
				responses.add(reading.trim());
			}
			
			writer.close();
			reader.close();
			socket.close();
			
			return responses;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Set<EventEntry> getEvents()
	{
		Set<EventEntry> ret = new HashSet<EventEntry>();
		List<String> response = sendMessages("LISTENTRIES");
		
		try {
			if(response != null && response.size() > 1)
			{
				int amount = Integer.parseInt(response.get(0).split(SharedData.SPLITTER)[1]);
				
				for(int i = 0; i < amount; i++)
				{
					EventEntry entry = EventEntry.createFromCSV(response.get(i+1).split(SharedData.SPLITTER), 0);
					
					if(entry != null)
					{
						ret.add(entry);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public Response login(String username, String password)
	{
		List<String> response = sendMessages("AUTH," + username + "," + password);
		
		try {
			if(response != null)
			{
				String[] split = response.get(0).split(SharedData.SPLITTER);
				
				if(split[0].equals("ACCEPT"))
				{
					SessionData.username = username;
					return Response.ACCEPT;
				}
				else {
					return new Response(false, split[1]);
				}
			}
		} catch(Exception e) {
			SessionData.reset();
			e.printStackTrace();
		}
		
		return Response.ERROR;
	}
	
	public Response register(String username, String password)
	{
		List<String> response = sendMessages("REGISTER," + username + "," + password);
		
		try {
			if(response != null)
			{
				String[] split = response.get(0).split(SharedData.SPLITTER);
				
				if(split[0].equals("ACCEPT"))
				{
					SessionData.username = username;
					return Response.ACCEPT;
				}
				else {
					return new Response(false, split[1]);
				}
			}
		} catch(Exception e) {
			SessionData.reset();
			e.printStackTrace();
		}
		
		return Response.ERROR;
	}
	
	public Response addEvent(EventEntry entry)
	{
		List<String> response = sendMessages("ADDEVENT," + entry.toCSV());
		
		try {
			if(response != null)
			{
				String[] split = response.get(0).split(SharedData.SPLITTER);
				return split[0].equals("ACCEPT") ? Response.ACCEPT : new Response(false, split[1]);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return Response.ERROR;
	}
	
	public Response editEvent(String origName, EventEntry entry)
	{
		List<String> response = sendMessages("EDITEVENT," + origName + "," + entry.toCSV());
		
		try {
			if(response != null)
			{
				String[] split = response.get(0).split(SharedData.SPLITTER);
				return split[0].equals("ACCEPT") ? Response.ACCEPT : new Response(false, split[1]);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return Response.ERROR;
	}
	
	public static class Response
	{
		public static final Response ERROR = new Response(false, "Error");
		public static final Response ACCEPT = new Response(true, null);
		
		public boolean accept;
		public String message;
		
		public Response(boolean accept, String message)
		{
			this.accept = accept;
			this.message = message;
		}
	}
}