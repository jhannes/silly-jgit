package org.sillygit.core.objects;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.sillygit.util.Util;


public class GitCommitObject extends GitObject {

	public GitCommitObject(File repository, String hash) {
		super(repository, hash);
	}

	public String getTreeHash() {
		String[] lines = toString().split("\n");
		return lines[0].split(" ")[2];
	}

	@Override
	public String toString() {
		try(final InputStream objectStream = getObjectStream()) {
			return Util.asString(new InputStreamReader(objectStream));
		} catch (IOException e) {
			return "Unreadable GitCommitObject " + hash + ": " + e.getMessage();
		}
	}

	public GitTreeObject getTreeObject() throws IOException {
		return new GitTreeObject(repository, getTreeHash(), "", "");
	}

	@Override
	public String dump() throws IOException {
		return super.dump();
	}
}
