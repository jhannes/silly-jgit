package org.sillygit.core;

import java.io.File;
import java.io.IOException;

import org.sillygit.core.objects.GitCommitObject;
import org.sillygit.util.Util;


public class GitRepository {

	private final File repository;

	public GitRepository(File repository) {
		this.repository = repository;
	}

	public String getRefHash(String ref) throws IOException {
		return Util.asString(new File(repository, ref)).trim();
	}

	public String getHeadRef() throws IOException {
		return Util.asString(new File(repository, "HEAD")).split(" ")[1].trim();
	}

	public GitCommitObject getCommitObject(String hash) {
		return new GitCommitObject(repository, hash);
	}

}
