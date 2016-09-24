package com.aidancbrady.swagslist.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import com.aidancbrady.swagslist.Account;
import com.aidancbrady.swagslist.EventEntry;
import com.aidancbrady.swagslist.SharedData;

public class SQLHandler
{
	private static ResultSet runQuery(String query)
	{
		try {
			Connection con = DriverManager.getConnection(SharedData.SQL_URL, SharedData.SQL_USER, SharedData.SQL_PASS);
	        PreparedStatement pst = con.prepareStatement(query);
	        return pst.executeQuery();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void runUpdate(String query)
	{
		try {
			Connection con = DriverManager.getConnection(SharedData.SQL_URL, SharedData.SQL_USER, SharedData.SQL_PASS);
	        PreparedStatement pst = con.prepareStatement(query);
            pst.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Account lookup(String username)
	{
		ResultSet results = runQuery("SELECT * FROM accounts WHERE username = '" + username + "'");

		try {
			while(results.next())
			{
				return new Account(results.getString(1), results.getString(2), results.getString(3));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void addAccount(Account account)
	{
		runUpdate("INSERT INTO accounts VALUES(" + 
				"'" + account.getUsername() + "'," +
				"'" + account.getPasswordHash() + "'," +
				"'" + account.getPasswordSalt() + "')");
	}
	
	public static void addEvent(EventEntry entry)
	{
		runUpdate("INSERT INTO events VALUES(" + 
				"'" + entry.getName() + "'," +
				"'" + entry.getDescription() + "'," +
				"'" + entry.getOwnerUsername() + "'," +
				entry.isPremium() + "," +
				entry.getLatitude() + "," +
				entry.getLongitude() + "," +
				entry.getStartTime() + "," +
				entry.getEndTime() + "," +
				entry.getSwagSetCSV() + ")");
	}
	
	public static Set<EventEntry> getEvents()
	{
		Set<EventEntry> ret = new HashSet<EventEntry>();
		ResultSet results = runQuery("SELECT * FROM events");
		
		try {
			while(results.next())
			{
				EventEntry entry = EventEntry.createFromSQL(results);
				
				if(entry != null)
				{
					ret.add(entry);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static boolean containsEvent(String eventName)
	{
		ResultSet results = runQuery("SELECT * FROM events WHERE name = '" + eventName + "'");
		
		try {
			return results.next();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void removeEvent(String eventName)
	{
		runUpdate("DELETE FROM events WHERE name = '" + eventName + "'");
	}
}
