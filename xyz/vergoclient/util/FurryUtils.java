package xyz.vergoclient.util;

public class FurryUtils {
	
	public static String getFurryText() {
		RandomObjectArraylist<String> OwO = new RandomObjectArraylist<String>("OwO", "UwU", "Awoo", "�w�", "�w�", "�w�", "�w�", ":3", "Rawr");
		return OwO.getRandomObject();
	}
	
	public static String getFurryTextSplashScreen() {
		RandomObjectArraylist<String> OwO = new RandomObjectArraylist<String>("OwO", "UwU", ":3", "Cum in me OwO",
				"Cum in me UwU", "Lean me over a table and fuck me", "Feeling submissive and breedable :3",
				"Now with 90% more gato", "Join r/hummusclient on reddit", "https://hummusclient.info",
				"https://e621.net");
		return OwO.getRandomObject();
	}
	
}
