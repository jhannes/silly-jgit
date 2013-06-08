package org.sillygit.core.objects;
import java.io.File;

public abstract class GitFileObject extends GitObject {

	protected final String path;
	protected final String octalMode;

	public GitFileObject(File repository, String hash, String octalMode, String path) {
		super(repository, hash);
		this.octalMode = octalMode;
		this.path = path;
	}

	public String getOctalMode() {
		return octalMode;
	}

	public String getPath() {
		return path;
	}

	public abstract String getType();

	public abstract GitFileObject getEntry(String string);



}