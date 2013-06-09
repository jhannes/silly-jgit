package org.sillygit.core.objects;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.sillygit.util.Util;


public class GitBlobObject extends GitFileObject {

	private String content;

	public GitBlobObject(File repository, String hash, String octalMode, String path) {
		super(repository, hash, octalMode, path);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<hash=" + hash + ",name=" + path + ">";
	}

	@Override
	public GitObject getEntry(String string) {
		throw new RuntimeException("Can't get entries in blob");
	}

	public String getContent() throws IOException {
		readContent();
		return content;
	}

	@Override
	protected void readContent() throws IOException {
		try(final InputStream objectStream = getObjectStream()) {
			readHeader(objectStream);
			content = Util.asString(objectStream);
		}
	}


}
