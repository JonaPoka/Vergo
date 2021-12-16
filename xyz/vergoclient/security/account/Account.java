package xyz.vergoclient.security.account;

import com.google.gson.annotations.SerializedName;

public class Account {
	
	public Account(int id, String email, String username, String password, String token, boolean emailVerified, boolean isAdmin, boolean hasBeta) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
		this.token = token;
		this.emailVerified = emailVerified;
		this.isAdmin = isAdmin;
		this.hasBeta = hasBeta;
	}
	
	@SerializedName(value = "id")
	public int id;
	
	@SerializedName(value = "email")
	public String email;
	
	@SerializedName(value = "username")
	public String username;
	
	@SerializedName(value = "password")
	public String password;
	
	@SerializedName(value = "token")
	public String token;
	
	@SerializedName(value = "emailVerified")
	public boolean emailVerified;
	
	@SerializedName(value = "isAdmin")
	public boolean isAdmin;
	
	@SerializedName(value = "hasBeta")
	public boolean hasBeta;
	
}
