package org.sillygit.core.objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.InflaterInputStream;

import org.sillygit.util.Util;

public class GitObject {

	protected String hash;
	protected File repository;

	public GitObject(File repository, String hash) {
		this.repository = repository;
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	protected File objectFile(String hash) {
		return new File(repository, "objects/" + hash.substring(0,2) + "/" + hash.substring(2));
	}

	protected InflaterInputStream getObjectStream() throws FileNotFoundException {
		return new InflaterInputStream(new FileInputStream(objectFile(hash)));
	}

	public String dump() throws IOException {
		try(final InputStream objectStream = getObjectStream()) {
			return Util.asString(new InputStreamReader(objectStream));
		}
	}

}