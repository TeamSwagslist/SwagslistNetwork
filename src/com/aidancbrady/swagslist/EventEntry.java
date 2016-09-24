package com.aidancbrady.swagslist;

import java.sql.ResultSet;
import java.util.EnumSet;
import java.util.Set;

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
	
	public static EventEntry createFromCSV(String[] data, int start)
	{
		EventEntry entry = new EventEntry();
		
		if(data.length-start != 6)
		{
			return null;
		}
		
		entry.name = data[start];
		entry.description = data[start+1].replace(SharedData.NEWLINE, "\n");
		entry.ownerUsername = data[start+2];
		entry.premium = data[start+3].equals("true");
		entry.latitude = Double.parseDouble(data[start+4]);
		entry.longitude = Double.parseDouble(data[start+5]);
		entry.startTime = Long.parseLong(data[start+6]);
		entry.endTime = Long.parseLong(data[start+7]);
		entry.parseSwagSetCSV(data[start+8]);
		
		String[] swagSplit = data[start+8].split(SharedData.SPLITTER_2);
		
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
	
	public static EventEntry createFromSQL(ResultSet results)
	{
		try {
			EventEntry entry = new EventEntry();
			entry.name = results.getString("name");
			entry.description = results.getString("description");
			entry.ownerUsername = results.getString("owner");
			entry.premium = results.getBoolean("premium");
			entry.latitude = results.getDouble("latitude");
			entry.longitude = results.getDouble("longitude");
			entry.startTime = results.getLong("startTime");
			entry.endTime = results.getLong("endTime");
			entry.parseSwagSetCSV(results.getString("swagSet"));
			return entry;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String toCSV()
	{
		return name + SharedData.SPLITTER + 
				description.replace("\n", SharedData.NEWLINE) + SharedData.SPLITTER +
				ownerUsername + SharedData.SPLITTER +
				premium + SharedData.SPLITTER +
				latitude + SharedData.SPLITTER +
				longitude + SharedData.SPLITTER +
				startTime + SharedData.SPLITTER +
				endTime + SharedData.SPLITTER +
				getSwagSetCSV();
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
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public Set<SwagType> getSwagSet()
	{
		return swagSet;
	}
	
	public String getSwagSetCSV()
	{
		StringBuilder b = new StringBuilder();
		
		for(SwagType type : swagSet) b.append(type.name() + ",");
		if(b.length() > 0) b.deleteCharAt(b.length()-1);
		
		return b.toString();
	}
	
	public void parseSwagSetCSV(String csv)
	{
		String[] swagSplit = csv.split(SharedData.SPLITTER_2);
		
		for(String s : swagSplit)
		{
			for(SwagType type : SwagType.values())
			{
				if(type.name().equals(s))
				{
					swagSet.add(type);
				}
			}
		}
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
