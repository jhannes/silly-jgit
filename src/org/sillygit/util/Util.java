package org.sillygit.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class Util {

	public static String asString(Reader reader) throws IOException {
		BufferedReader lineReader = new BufferedReader(reader);
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = lineReader.readLine()) != null) {
			result.append(line).append('\n');
		}
		return result.toString();
	}

	public static String asString(File file) throws IOException {
		try (Reader reader = new FileReader(file)) {
			return asString(reader);
		}
	}

	public static String stringUntil(InputStream inputStream, char terminator) throws IOException {
		StringBuilder result = new StringBuilder();
		int c;
		while ((c = inputStream.read()) != -1 && c != terminator) {
			result.append((char)c);
		}
		return result.length() > 0 ? result.toString() : null;
	}

	public static String leftPad(String string, int length, char c) {
		if (string == null) return null;
		StringBuilder result = new StringBuilder();
		while (length-- > string.length()) result.append(c);
		return result + string;
	}

}
