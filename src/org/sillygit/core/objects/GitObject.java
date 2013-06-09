package org.sillygit.core.objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import org.sillygit.util.Util;

public abstract class GitObject {

	protected String hash;
	protected File repository;
	private Long length;
	private String type;

	public GitObject(File repository, String hash) {
		this.repository = repository;
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public Long getLength() throws IOException {
		readContent();
		return length;
	}

	public String getType() throws IOException {
		readContent();
		return type;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + getHash() + "}";
	}

	public String dump() throws IOException {
		try(final InputStream objectStream = getObjectStream()) {
			return Util.asString(objectStream);
		}
	}

	protected abstract void readContent() throws IOException;

	protected InflaterInputStream getObjectStream() throws FileNotFoundException {
		return new InflaterInputStream(new FileInputStream(getObjectFile(hash)));
	}

	private File getObjectFile(String hash) {
		return new File(repository, "objects/" + hash.substring(0,2) + "/" + hash.substring(2));
	}

	protected void readHeader(InputStream inputStream) throws IOException {
		this.type = Util.stringUntil(inputStream, ' ');
		this.length = Long.valueOf(Util.stringUntil(inputStream, (char)0));
	}

}