package org.sillygit.core.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;

import org.sillygit.util.Util;

public class GitObjectOutputStream extends FilterOutputStream {

	private final MessageDigest digest;
	private File tempFile;
	private final File repository;
	private String hash;
	private final String type;
	private int length;

	public GitObjectOutputStream(File repository, String type) throws IOException {
		this(repository, type, File.createTempFile("gitobject", null), createDigest());
	}

	private GitObjectOutputStream(File repository, String type, File tempFile, MessageDigest digest) throws IOException {
		super(new DigestOutputStream(new FileOutputStream(tempFile), digest));
		this.repository = repository;
		this.type = type;
		this.tempFile = tempFile;
		this.digest = digest;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		this.length++;
	}

	@Override
	public void close() throws IOException {
		if (tempFile == null) return;
		super.flush();

		this.hash = Util.asHexString(digest.digest());
		File objectFile =
				new File(repository, "objects/" + hash.substring(0,2) + "/" + hash.substring(2));
		objectFile.getParentFile().mkdirs();
		try (OutputStream objectOutput = new DeflaterOutputStream(new FileOutputStream(objectFile))) {
			objectOutput.write(type.getBytes());
			objectOutput.write(' ');
			objectOutput.write(String.valueOf(length).getBytes()); // TODO < this should be the size
			objectOutput.write((byte)0);

			try (FileInputStream input = new FileInputStream(tempFile)) {
				Util.copy(input, objectOutput);
			}
		}

		super.close();
		tempFile.delete();
		tempFile = null;
	}

	public String closeAndGetHash() throws IOException {
		close();
		return hash;
	}

	private static MessageDigest createDigest() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 should always be supported: " + e);
		}
	}


}
