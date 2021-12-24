package xyz.vergoclient.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.ui.guis.GuiStart;

public class ChatFilterBypassUtils {

	public static String bypassWords(String input, String bypassChars, int period) {
//		input = input.replaceAll("a", "@");
//		input = input.replaceAll("A", "@");
//		input = input.replaceAll("e", "€");
//		input = input.replaceAll("E", "€");
		ArrayList<String> badWords = findWords(input);
		for (String word : badWords) {
			input = input.replaceFirst(word, insertPeriodically(word, bypassChars, period));
		}
		input = input.replaceAll("EZ", insertPeriodically("EZ", bypassChars, period));
		input = input.replaceAll("ez", insertPeriodically("ez", bypassChars, period));
		input = input.replaceAll("Ez", insertPeriodically("Ez", bypassChars, period));
		input = input.replaceAll("eZ", insertPeriodically("eZ", bypassChars, period));
		return input;
	}
	
	public static String insertPeriodically(String text, String insert, int period) {
		StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);

		int index = 0;
		String prefix = "";
		while (index < text.length()) {
			// Don't put the insert in the very first iteration.
			// This is easier than appending it *after* each substring
			builder.append(prefix);

			Random random = new Random();

			// String bypass = ⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖❎;
			prefix = Character.toString(insert.charAt(random.nextInt(insert.length())));

			builder.append(text.substring(index, Math.min(index + period, text.length())));
			index += period;
		}
		
		return builder.toString();
	}
	
	// Github basic word finder go brrr

	private static Map<String, String[]> words = new HashMap<>();

	private static int largestWordLength = 0;

	private static ArrayList<String> findWords(String input) {
		if (input == null) {
			return new ArrayList<>();
		}

		// don't forget to remove leetspeak, probably want to move this to its own
		// function and use regex if you want to use this

		input = input.replaceAll("1", "i");
		input = input.replaceAll("!", "i");
		input = input.replaceAll("3", "e");
		input = input.replaceAll("4", "a");
		input = input.replaceAll("@", "a");
		input = input.replaceAll("5", "s");
		input = input.replaceAll("7", "t");
		input = input.replaceAll("0", "o");
		input = input.replaceAll("9", "g");

		ArrayList<String> badWords = new ArrayList<>();
		input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

		// iterate over each letter in the word
		for (int start = 0; start < input.length(); start++) {
			// from each letter, keep going to find bad words until either the end of the
			// sentence is reached, or the max word length is reached.
			for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
				String wordToCheck = input.substring(start, start + offset);
				if (words.containsKey(wordToCheck)) {
					// for example, if you want to say the word bass, that should be possible.
					String[] ignoreCheck = words.get(wordToCheck);
					boolean ignore = false;
					for (int s = 0; s < ignoreCheck.length; s++) {
						if (input.contains(ignoreCheck[s])) {
							ignore = true;
							break;
						}
					}
					if (!ignore) {
						badWords.add(wordToCheck);
					}
				}
			}
		}

		for (String s : badWords) {
//			System.out.println(s + " qualified as a bad word in a username");
		}
		return badWords;

	}

}
