package com.aidancbrady.swagslist;

import java.util.EnumSet;
import java.util.Set;

import com.aidancbrady.swagslist.network.Connection;

public class EventEntry
{
	private String name;
	
	private String description;
	
	private String ownerUsername;
	
	private boolean premium;
	
	private double latitude;
	private double longitude;
	
	private long startTime;
	private long endTime;
	
	private EnumSet<SwagType> swagSet = EnumSet.noneOf(SwagType.class);
	
	public static EventEntry createEntry(String[] data, int start)
	{
		EventEntry entry = new EventEntry();
		
		if(data.length-start != 6)
		{
			return null;
		}
		
		entry.name = data[start];
		entry.description = data[start+1];
		entry.ownerUsername = data[start+2];
		entry.premium = data[start+3].equals("true");
		entry.latitude = Double.parseDouble(data[start+4]);
		entry.longitude = Double.parseDouble(data[start+5]);
		entry.startTime = Long.parseLong(data[start+6]);
		entry.endTime = Long.parseLong(data[start+7]);
		
		String[] swagSplit = data[start+8].split(Connection.SPLITTER_2);
		
		for(String s : swagSplit)
		{
			for(SwagType type : SwagType.values())
			{
				if(type.name().equals(s))
				{
					entry.swagSet.add(type);
				}
			}
		}
		
		return entry;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getOwnerUsername()
	{
		return ownerUsername;
	}
	
	public boolean isPremium()
	{
		return premium;
	}
	
	public Set<SwagType> getSwagSet()
	{
		return swagSet;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
	public long getEndTime()
	{
		return endTime;
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		
		EventEntry other = (EventEntry)obj;
		
		return name.equals(other.name);
	}

	public static enum SwagType
	{
		FOOD,
		APPAREL,
		TRINKETS;
	}
}
