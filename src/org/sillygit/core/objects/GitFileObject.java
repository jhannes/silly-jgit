package org.sillygit.core.objects;
import java.io.File;
import java.io.IOException;

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

	public abstract GitFileObject getEntry(String string) throws IOException;

	public abstract String getContent() throws IOException;

	public byte[] getBinaryHash() {
		byte[] result = new byte[20];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte)(Integer.parseInt(getHash().substring(i*2, i*2+2), 0x10) & 0xff);
		}
		return result;
	}

}