package org.sillygit.core.objects;
import java.io.File;


public class GitBlobObject extends GitFileObject {

	public GitBlobObject(File repository, String hash, String octalMode, String path) {
		super(repository, hash, octalMode, path);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<hash=" + hash + ",name=" + path + ">";
	}

	@Override
	public String getType() {
		return "blob";
	}

	@Override
	public GitFileObject getEntry(String string) {
		throw new RuntimeException("Can't get entries in blob");
	}

}
