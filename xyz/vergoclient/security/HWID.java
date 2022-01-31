package xyz.vergoclient.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWID {

	public static String getHWIDForWindows() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String s = "";
		String main = System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim() + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
		byte[] bytes = main.getBytes("UTF-8");
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] md5 = messageDigest.digest(bytes);
		int i = 0;
		for (byte b : md5) {
			s = s + Integer.toHexString(b & 0xFF | 0x300).substring(0, 3);
			i++;
		}
		return s;
	}
	
}