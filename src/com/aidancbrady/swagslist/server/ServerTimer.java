package com.aidancbrady.swagslist.server;

import java.util.HashSet;
import java.util.Set;

import com.aidancbrady.swagslist.EventEntry;

public final class ServerTimer extends Thread
{
	public ServerTimer()
	{
		setDaemon(true);
	}
	
	@Override
	public void run()
	{
		while(ServerCore.instance().isRunning())
		{
			try {
				Thread.sleep(1000*60);
				long currentMillis = System.currentTimeMillis();
				
				Set<EventEntry> events = SQLHandler.getEvents();
				Set<String> toRemove = new HashSet<String>();
				
				for(EventEntry entry : events)
				{
					if(currentMillis > entry.getEndTime())
					{
						toRemove.add(entry.getName());
					}
				}
				
				for(String name : toRemove)
				{
					SQLHandler.removeEvent(name);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
