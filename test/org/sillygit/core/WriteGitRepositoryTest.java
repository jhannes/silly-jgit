package org.sillygit.core;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sillygit.core.objects.GitBlobObject;
import org.sillygit.core.objects.GitCommitObject;
import org.sillygit.core.objects.GitTreeObject;

public class WriteGitRepositoryTest {

	private static final String AUTHOR = "Johannes Brodwall <jbr@exilesoft.com>";

	private final File repodir = new File("tmp/testrepo/.git");

	private final GitRepository repository = GitRepository.init(repodir);

	public WriteGitRepositoryTest() throws IOException {
	}

	@Test
	public void shouldCreateRepository() throws Exception {
		assertThat(repodir).exists();
		assertThat(repository.getHeadRef()).isEqualTo("refs/heads/master");
		assertThat(repository.getRefHash("refs/heads/master")).isNull();
	}

	@Test
	public void shouldCreateBlobObject() throws Exception {
		GitBlobObject savedBlob = repository.writeBlob("foobar.txt", "This is a test");
		String hash = savedBlob.getHash();
		assertThat(hash).matches("[0-9a-z]{40}");
		assertThat(savedBlob.getObjectFile()).isFile();

		GitBlobObject retrievedBlob = new GitBlobObject(repodir, hash, "", "");
		assertThat(retrievedBlob.getContent()).isEqualTo("This is a test");
	}

	@Test
	public void shouldCreateDirectoryObject() throws Exception {
		GitBlobObject file1 = repository.writeBlob("file1.txt", "This is a test");
		GitBlobObject file2 = repository.writeBlob("file2.txt", "This is a test");
		GitTreeObject tree = repository.writeTree("", file1, file2);

		tree = new GitTreeObject(repodir, tree.getHash(), "", "");
		assertThat(tree.getEntryPaths()).containsOnly("file1.txt", "file2.txt");
	}

	@Test
	public void shouldWriteCommit() throws Exception {
		GitCommitObject commit = repository.writeCommit(repository.writeTree(""), AUTHOR, "Hello");

		System.out.println(commit.getHash());
		commit = new GitCommitObject(repodir, commit.getHash());
		assertThat(commit.getAuthor() + " <" + commit.getAuthorEmail() + ">").isEqualTo(AUTHOR);
		assertThat(commit.getComment()).isEqualTo("Hello");
		assertThat(commit.getParentCommit()).isNull();
		assertThat(commit.getTreeObject().getEntryPaths()).isEmpty();
	}

	@Test
	public void shouldWriteSeveralCommits() throws Exception {
		GitRepository repository = createTestRepository();

		GitCommitObject commit = repository.getHeadCommit();
		assertThat(commit.getComment()).isEqualTo("Second commit");
		assertThat(commit.getTreeObject().getEntryPaths()).containsOnly("README.md", "src");
		assertThat(commit.getTreeObject().getEntry("README.md").getContent())
			.isEqualTo("This is another test");
		assertThat(commit.getTreeObject().getEntry("src")).isInstanceOf(GitTreeObject.class);
		assertThat(commit.getTreeObject().getEntry("src").getEntry("test.rb").getContent())
			.isEqualTo("puts 'hello world'\n");
	}

	private GitRepository createTestRepository() throws IOException {
		GitBlobObject file1 = repository.writeBlob("README.md", "This is a test");
		GitBlobObject file2 = repository.writeBlob("test.rb", "puts 'hello world'\n");
		GitTreeObject srcTree = repository.writeTree("src", file2);
		GitTreeObject commitTree = repository.writeTree("", file1, srcTree);
		GitCommitObject commit = repository.writeCommit(commitTree, AUTHOR, "Initial commit");

		GitBlobObject file1Changed = repository.writeBlob("README.md", "This is another test");
		GitTreeObject commit2Tree = repository.writeTree("", file1Changed, srcTree);
		GitCommitObject commit2 = repository.writeCommit(commit2Tree, AUTHOR, "Second commit", commit);
		repository.setHeadCommit(repository.getHeadRef(), commit2);
		return repository;
	}

}
