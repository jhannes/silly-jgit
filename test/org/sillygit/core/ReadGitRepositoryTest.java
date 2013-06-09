package org.sillygit.core;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;
import org.sillygit.core.objects.GitBlobObject;
import org.sillygit.core.objects.GitCommitObject;
import org.sillygit.core.objects.GitTreeObject;

public class ReadGitRepositoryTest {

	private final GitRepository repository = new GitRepository(new File(".git"));
	private static String someCommitHash = "5aca7298b5b52210073b599897ff82fe767c6f8c";
	private static String someTreeHash = "c386f6f54e33b72b1ca37e8f24d852d931c1574d";

	@Test
	public void shouldFindHead() throws Exception {
		assertThat(repository.getHeadRef()).matches("refs/heads/.*");
	}

	@Test
	public void shouldFindHash() throws Exception {
		assertThat(repository.getRefHash("refs/heads/master")).matches("[0-9a-f]{40}");
	}

	@Test
	public void shouldGetCommitObject() throws Exception {
		GitCommitObject commit = repository.getCommitObject(someCommitHash);
		assertThat(commit.getHash()).isEqualTo(someCommitHash);

		assertThat(commit.getTreeHash()).isEqualTo(someTreeHash);
		assertThat(commit.getType()).isEqualTo("commit");
		assertThat(commit.getParentHash())
			.isEqualTo("25c900127cb1d3b26041ee0fef814d53227b2ffb");
		assertThat(commit.getAuthor()).isEqualTo("Johannes Brodwall");
		assertThat(commit.getAuthorEmail()).isEqualTo("jbr@exilesoft.com");
		assertThat(commit.getAuthorTime()).isInSameDayAs("2013-06-08T18:59:28");
		assertThat(commit.getCommitter()).isEqualTo("Johannes Brodwall");
		assertThat(commit.getCommitterEmail()).isEqualTo("jbr@exilesoft.com");
		assertThat(commit.getCommitterTime()).isInSameDayAs("2013-06-08T18:59:28");
		assertThat(commit.getComment()).isEqualTo("First draft version\n");
	}

	@Test
	public void shouldGetOrphanCommitObject() throws Exception {
		GitCommitObject commit = repository.getCommitObject(someCommitHash);
		GitCommitObject parent = commit.getParentCommit();
		assertThat(parent.getParentHash()).isNull();
		assertThat(parent.getParentCommit()).isNull();
		assertThat(parent.getComment()).isEqualTo("Initial commit\n");
	}

	@Test
	public void shouldGetTree() throws Exception {
		GitTreeObject treeObject = repository.getCommitObject(someCommitHash).getTreeObject();
		assertThat(treeObject.getEntryPaths()).contains("README.md", ".project", ".classpath", "src");
		assertThat(treeObject.getEntry("README.md")).isInstanceOf(GitBlobObject.class);
		assertThat(treeObject.getEntry("README.md").getOctalMode()).isEqualTo("100644");
		assertThat(treeObject.getEntry("src")).isInstanceOf(GitTreeObject.class);
		assertThat(treeObject.getEntry("src").getOctalMode()).isEqualTo("040000");
		assertThat(treeObject.getEntry("src").getType()).isEqualTo("tree");
	}

	@Test
	public void shouldGetFileContent() throws Exception {
		GitTreeObject treeObject = repository.getCommitObject(someCommitHash).getTreeObject();
		assertThat(treeObject.getEntry("README.md").getContent())
			.isEqualTo("silly-jgit\n==========\n\nA pure java implementation of the core of a Git library\n");
		assertThat(treeObject.getEntry("README.md").getLength()).isEqualTo(79L);
		assertThat(treeObject.getEntry("README.md").getType()).isEqualTo("blob");
	}

	@Test
	public void shouldGetSubTree() throws Exception {
		GitTreeObject treeObject = repository.getCommitObject(someCommitHash).getTreeObject();
		GitTreeObject treeForSrc = (GitTreeObject) treeObject.getEntry("src");
		assertThat(treeForSrc.getEntryPaths()).containsOnly("SillyGit.java", "org");
		assertThat(treeForSrc.getType()).isEqualTo("tree");
		assertThat(treeForSrc.getLength()).isEqualTo(71L);
		assertThat(treeObject.getEntry("src").getEntry("org")).isInstanceOf(GitTreeObject.class);
	}

}
