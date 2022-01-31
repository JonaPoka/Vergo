package xyz.vergoclient.security.account;

import com.google.gson.annotations.SerializedName;

public class Account {
	
	public Account(int uid, String hwid, int banned) {
		this.uid = uid;
		this.hwid = hwid;
		this.banned = banned;
	}
	
	@SerializedName(value = "uid")
	public int uid;
	
	@SerializedName(value = "hwid")
	public String hwid;
	
	@SerializedName(value = "banned")
	public int banned;
	
}
