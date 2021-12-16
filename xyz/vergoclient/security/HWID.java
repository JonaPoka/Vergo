package xyz.vergoclient.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class HWID {
	
	public static String getHWID() {
		
		String hwid = "";
		
		// Gets the computer's hardware data
//		Removed because this only works on windows
//		hwid += execCmd("wmic baseboard get *");
//		hwid += execCmd("wmic cpu get name, Manufacturer, Caption, DeviceID");
//		hwid += execCmd("wmic memorychip get speed");
		hwid += System.getProperty("user.name");
		hwid += System.getProperty("os.name");
		hwid += System.getenv("PROCESSOR_IDENTIFIER");
		hwid += System.getenv("PROCESSOR_LEVEL");
		hwid += System.getenv("COMPUTERNAME");
		hwid += System.getProperties().toString();
		
		// Removes spaces and end of line characters
		hwid = hwid.replaceAll(" ", "").replaceAll("(\\r|\\n)", "");
		
		// From github somewhere
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(hwid.getBytes());
	        StringBuffer hexString = new StringBuffer();
	        
	        byte byteData[] = md.digest();
	        
	        for (byte aByteData : byteData) {
	            String hex = Integer.toHexString(0xff & aByteData);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        hwid = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// Return
		return hwid;
		
	}
	
}