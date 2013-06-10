package org.sillygit.core.objects;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import org.sillygit.util.Util;


public class GitCommitObject extends GitObject {

	private String treeHash;
	private String parentHash;
	private String author;
	private String authorEmail;
	private String committer;
	private String committerEmail;
	private Date authorTime;
	private Date committerTime;
	private String comment;

	public GitCommitObject(File repository, String hash) {
		super(repository, hash);
	}

	public GitTreeObject getTreeObject() throws IOException {
		return new GitTreeObject(repository, getTreeHash(), "", "");
	}

	public GitCommitObject getParentCommit() throws IOException {
		if (getParentHash() == null) return null;
		return new GitCommitObject(repository, getParentHash());
	}

	public String getTreeHash() throws IOException {
		readContent();
		return treeHash;
	}

	public String getParentHash() throws IOException {
		readContent();
		return parentHash;
	}

	public String getAuthor() throws IOException {
		readContent();
		return author;
	}

	public String getAuthorEmail() throws IOException {
		readContent();
		return authorEmail;
	}

	public Date getAuthorTime() throws IOException {
		readContent();
		return authorTime;
	}

	public String getCommitter() throws IOException {
		readContent();
		return committer;
	}

	public String getCommitterEmail() throws IOException {
		readContent();
		return committerEmail;
	}

	public Date getCommitterTime() throws IOException {
		readContent();
		return committerTime;
	}

	public String getComment() throws IOException {
		readContent();
		return comment;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<" + getHash() + ">";
	}

	@Override
	protected void readContent() throws IOException {
		if (treeHash != null) return;
		try(final InputStream inputStream = getObjectStream()) {
			readHeader(inputStream);
			Util.stringUntil(inputStream, ' ');
			this.treeHash = Util.stringUntil(inputStream, '\n');
			String header = Util.stringUntil(inputStream, ' ');
			if (header.equals("parent")) {
				this.parentHash = Util.stringUntil(inputStream, '\n');
				header = Util.stringUntil(inputStream, ' ');
			}
			Util.validateEquals(header, "author");
			this.author = Util.stringUntil(inputStream, '<').trim();
			this.authorEmail = Util.stringUntil(inputStream, '>');
			inputStream.skip(1);
			long authorTime = Long.parseLong(Util.stringUntil(inputStream, ' '));
			Util.stringUntil(inputStream, '\n');
			this.authorTime = new Date(authorTime*1000);

			Util.validateEquals(Util.stringUntil(inputStream, ' '), "committer");
			this.committer = Util.stringUntil(inputStream, '<').trim();
			this.committerEmail = Util.stringUntil(inputStream, '>');
			inputStream.skip(1);
			long committerTime = Long.parseLong(Util.stringUntil(inputStream, ' '));
			Util.stringUntil(inputStream, '\n');
			this.committerTime = new Date(committerTime*1000);

			Util.stringUntil(inputStream, '\n');
			this.comment = Util.asString(inputStream);
		}
	}

	public static GitCommitObject writeCommit(File repository, GitTreeObject commitTree, String author, String comment, GitCommitObject parent) throws IOException {
		GitCommitObject commit = new GitCommitObject(repository, null);
		try (GitObjectOutputStream output = new GitObjectOutputStream(repository, "commit")) {
			output.write(("tree " + commitTree.getHash() + "\n").getBytes());
			if (parent != null) {
				output.write(("parent " + parent.getHash() + "\n").getBytes());
			}
			output.write(("author " + author + " " + System.currentTimeMillis()/1000 + " +0" + "\n") .getBytes());
			output.write(("committer " + author + " " + System.currentTimeMillis()/1000 + " +0" + "\n") .getBytes());
			output.write('\n');
			output.write(comment.getBytes());
			commit.hash = output.closeAndGetHash();
		}
		return commit;
	}
}