package xyz.vergoclient.files.impl;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class OldSpicyAltInfoDontUseIgnoreUsedForImporting {
	
	@SerializedName(value = "alts")
	public ArrayList<Alt> alts = new ArrayList<OldSpicyAltInfoDontUseIgnoreUsedForImporting.Alt>();
	
	public static class Alt{
		
		public String username = "Log in to view the username";
		public String email;
		public String password;
		public boolean premium;
		public int status = 0;
		public long unbannedAt = 0;
		
		
		public Alt(String email, String password, boolean premium) {
			
			this.email = email;
			this.password = password;
			this.premium = premium;
			
		}
		
	}
	
}
