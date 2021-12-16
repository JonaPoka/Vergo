package xyz.vergoclient.userFinder;

public class HummusUser {
	
	public HummusUser() {
		
	}
	
	public HummusUser(String minecraftUsername, String hummusUsername) {
		this.minecraftUsername = minecraftUsername;
		this.hummusUsername = hummusUsername;
	}
	
	public String minecraftUsername, hummusUsername;
	
}
