package com.aidancbrady.swagslist;

import java.util.HashSet;
import java.util.Set;

public class SharedData 
{
	//Server data
	public static final String SERVER_IP = "104.236.13.142";
	public static final int SERVER_PORT = 29421;
	
	//SQL data
	public static final String SQL_URL = "jdbc:mysql://localhost:3306/swagslist";
	public static final String SQL_USER = "server";
	public static final String SQL_PASS = "hack_gt_db";
	
	//CSV splitters
	public static final String PRIME_SPLITTER = "&0&";
	public static final String SPLITTER = "%1%";
	public static final String SPLITTER_2 = "[2]";
	public static final String NEWLINE = "&NL&";
	
	public static final Set<Character> ALLOWED_CHARS = new HashSet<Character>();
	public static final int MAX_USERNAME_LENGTH = 24;
	
	static {
		ALLOWED_CHARS.add('-');
		ALLOWED_CHARS.add('_');
		ALLOWED_CHARS.add('.');
	}
}
