package com.aidancbrady.swagslist.network;

import java.util.Iterator;

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
				
				for(Iterator<EventEntry> iter = ServerCore.instance().getEvents().iterator(); iter.hasNext();)
				{
					EventEntry entry = iter.next();
					
					if(currentMillis > entry.getEndTime())
					{
						iter.remove();
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
