package com.aidancbrady.swagslist;

public class Account
{
	private String username;
	
	private String passwordSalt;
	private String passwordHash;
	
	public Account(String username)
	{
		this.username = username;
	}
	
	public Account(String username, String passwordSalt, String passwordHash)
	{
		this(username);
		
		this.passwordSalt = passwordSalt;
		this.passwordHash = passwordHash;
	}
	
	public boolean initPassword(String password)
	{
		try {
			passwordSalt = Security.genSalt();
			passwordHash = Security.hash(passwordSalt + password);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean checkPassword(String s)
	{
		try {
			return passwordHash.equals(Security.hash(passwordSalt + s));
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPasswordSalt()
	{
		return passwordSalt;
	}
	
	public String getPasswordHash()
	{
		return passwordHash;
	}
}
