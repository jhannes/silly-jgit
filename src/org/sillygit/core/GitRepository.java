package org.sillygit.core;

import java.io.File;
import java.io.IOException;

import org.sillygit.core.objects.GitBlobObject;
import org.sillygit.core.objects.GitCommitObject;
import org.sillygit.core.objects.GitFileObject;
import org.sillygit.core.objects.GitTreeObject;
import org.sillygit.util.Util;


public class GitRepository {

	private final File repository;

	public GitRepository(File repository) {
		this.repository = repository;
	}

	public String getHeadRef() throws IOException {
		return Util.asString(new File(repository, "HEAD")).split(" ")[1].trim();
	}

	public String getRefHash(String ref) throws IOException {
		File file = new File(repository, ref);
		return file.isFile() ? Util.asString(file).trim() : null;
	}

	private void setRefHash(String ref, String hash) throws IOException {
		Util.writeFile(new File(repository, ref), hash);
	}

	public GitCommitObject getCommitObject(String hash) {
		return new GitCommitObject(repository, hash);
	}

	public GitCommitObject getHeadCommit() throws IOException {
		return getCommitObject(getRefHash(getHeadRef()));
	}

	public void setHeadCommit(String headRef, GitCommitObject commit) throws IOException {
		setRefHash(headRef, commit.getHash());
	}


	public GitBlobObject writeBlob(String filename, String content) throws IOException {
		return GitBlobObject.writeFile(repository, filename, "100644", content);
	}

	public GitTreeObject writeTree(String filename, GitFileObject... entries) throws IOException {
		return GitTreeObject.writeTree(repository, filename, entries);
	}

	public GitCommitObject writeCommit(GitTreeObject tree, String author, String comment) throws IOException {
		return writeCommit(tree, author, comment, null);
	}

	public GitCommitObject writeCommit(GitTreeObject tree,
			String author, String comment, GitCommitObject parent) throws IOException {
		return GitCommitObject.writeCommit(repository, tree, author, comment, parent);
	}

	public static GitRepository init(File file) throws IOException {
		Util.recursiveDelete(file);
		file.mkdirs();
		new File(file, "refs/heads").mkdirs();
		Util.writeFile(new File(file, "HEAD"), "ref: refs/heads/master\n");
		return new GitRepository(file);
	}

	public File getFile(String hash) {
		return null;
	}

}
