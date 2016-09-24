package com.aidancbrady.swagslist.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
}