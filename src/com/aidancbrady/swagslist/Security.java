package com.aidancbrady.swagslist;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

public class Security 
{
	private static Random random = new SecureRandom();
	
	public static String hash(String password) throws Exception
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		
		byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		return getHex(hash);
	}
	
	private static String getHex(byte[] bytes)
	{
		StringBuffer hexString = new StringBuffer();

        for(int i = 0; i < bytes.length; i++) 
        {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
	}
	
	public static String genSalt()
	{
		byte[] salt = new byte[32];
		random.nextBytes(salt);
		return getHex(salt);
	}
}
