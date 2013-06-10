package org.sillygit.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static String asString(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		int c;
		while ((c = inputStream.read()) != -1) {
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

	public static void validateEquals(String actual, String expected) {
		if (!actual.equals(expected))
			throw new RuntimeException("Unexpected - <" + actual + "> should have been <" + expected + ">");
	}

	public static void writeFile(File file, String string) throws IOException {
		file.getParentFile().mkdirs();
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(string);
			writer.flush();
		}
	}

	public static String asHexString(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (int i=0; i<bytes.length; i++) {
			result.append(leftPad(Integer.toHexString(0xff & bytes[i]), 2, '0'));
		}
		return result.toString();
	}

	public static void recursiveDelete(File file) {
		if (file.isDirectory()) {
			for (File subfile : file.listFiles()) {
				recursiveDelete(subfile);
			}
		}
		file.delete();
	}

	public static void copy(FileInputStream input, OutputStream output) throws IOException {
		int c;
		while ((c = input.read()) != -1) output.write((byte)c);
	}


}
