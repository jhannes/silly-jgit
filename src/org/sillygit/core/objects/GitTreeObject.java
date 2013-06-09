package org.sillygit.core.objects;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.sillygit.util.Util;


public class GitTreeObject extends GitFileObject {

	private List<GitFileObject> entries;

	public GitTreeObject(File repository, String hash, String octalMode, String path) throws IOException {
		super(repository, hash, octalMode, path);
	}

	public List<GitFileObject> getEntries() throws IOException {
		readContent();
		return entries;
	}

	public List<String> getEntryPaths() throws IOException {
		ArrayList<String> result = new ArrayList<>();
		for (GitFileObject entry : getEntries()) {
			result.add(entry.getPath());
		}
		return result;
	}

	@Override
	public GitFileObject getEntry(String string) throws IOException {
		for (GitFileObject gitFileObject : getEntries()) {
			if (gitFileObject.getPath().equals(string)) return gitFileObject;
		}
		return null;
	}

	@Override
	public String getContent() throws IOException {
		return dump();
	}

	@Override
	public String dump() throws IOException {
		StringBuilder result = new StringBuilder();
		for (GitFileObject entry : getEntries()) {
			result.append(entry.getOctalMode() + " ")
				.append(entry.getType() + " ")
				.append(entry.getHash() + " ")
				.append(entry.getPath())
				.append("\n");
		}
		return result.toString();
	}

	@Override
	protected void readContent() throws IOException {
		if (entries != null) return;
		entries = new ArrayList<>();
		try(final InputStream inputStream = getObjectStream()) {
			readHeader(inputStream);

			GitFileObject entry;
			while ((entry = readEntry(inputStream)) != null){
				entries.add(entry);
			}
		}
	}

	private GitFileObject readEntry(InputStream inputStream) throws IOException {
		String octalMode = Util.leftPad(Util.stringUntil(inputStream, ' '), 6, '0');
		if (octalMode == null) return null;
		String path = Util.stringUntil(inputStream, (char)0);
		String hash = readBinaryObjectHash(inputStream);
		if (octalMode.startsWith("0")) {
			return new GitTreeObject(repository, hash, octalMode, path);
		} else {
			return new GitBlobObject(repository, hash, octalMode, path);
		}
	}

	private String readBinaryObjectHash(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		for (int i=0; i<20; i++) {
			result.append(Util.leftPad(Integer.toHexString(inputStream.read()), 2, '0'));
		}
		return result.toString();
	}
}
