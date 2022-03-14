package xyz.vergoclient.util;

import java.net.Proxy;
import java.util.UUID;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;

import xyz.vergoclient.security.ApiResponse;
import xyz.vergoclient.security.account.AccountUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SessionChanger {

	private static SessionChanger instance;
	private final UserAuthentication auth;

	public static SessionChanger getInstance() {
		if (instance == null) {
			instance = new SessionChanger();
		}

		return instance;
	}
	
	//Creates a new Authentication Service. 
	private SessionChanger() {
		UUID randomizedUUID = UUID.randomUUID();
		AuthenticationService authSer = new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), randomizedUUID.toString());
		auth = authSer.createUserAuthentication(Agent.MINECRAFT);
		authSer.createMinecraftSessionService();
	}
	
	//Online mode
	//Checks if your already logged in to the account.
	public void setUser(String email, String password) {
		
		String currentUsername = Minecraft.getMinecraft().session.getUsername();
		
		Proxy temp = Minecraft.getMinecraft().proxy;
		Minecraft.getMinecraft().proxy = Proxy.NO_PROXY;
		
		if(!Minecraft.getMinecraft().getSession().getUsername().equals(email) || Minecraft.getMinecraft().getSession().getToken().equals("0")){

			this.auth.logOut();
			this.auth.setUsername(email);
			this.auth.setPassword(password);
			try {
				this.auth.logIn();
				Session session = new Session(this.auth.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID(auth.getSelectedProfile().getId()), this.auth.getAuthenticatedToken(), this.auth.getUserType().getName());
				setSession(session);
				
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		Minecraft.getMinecraft().proxy = temp;
		
	}

	//Sets the session.
	//You need to make this public, and remove the final modifier on the session Object.
	private void setSession(Session session) {
		Minecraft.getMinecraft().session = session;
	}

	//Login offline mode
	//Just like MCP does
	public void setUserOffline(String username) {
		this.auth.logOut();
		Session session = new Session(username, username, "0", "legacy");
		setSession(session);
	}
}