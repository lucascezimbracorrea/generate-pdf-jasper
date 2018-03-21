package br.com.flucas.util;

public class StringUtils {

	public static int countOccurences(String haystack, String needle) {
		return haystack.length() - haystack.replace(needle, "").length();
	}
	
}
