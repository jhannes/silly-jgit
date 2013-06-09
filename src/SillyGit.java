import java.io.File;
import java.io.IOException;

import org.sillygit.core.GitRepository;
import org.sillygit.core.objects.GitCommitObject;
import org.sillygit.core.objects.GitTreeObject;


public class SillyGit {

	public static void main(String[] args) throws IOException {
		explore(new GitRepository(new File("C:\\Users\\johannes\\temp\\git_tutorial\\hello\\.git\\")));
	}

	private static void explore(GitRepository repo) throws IOException {
		String commitHash = repo.getRefHash(repo.getHeadRef());
		GitCommitObject commitObject = repo.getCommitObject(commitHash);
		System.out.println(commitObject.dump());
		GitTreeObject treeObject = commitObject.getTreeObject();
		System.out.println(treeObject.dump());
		System.out.println(treeObject);
		System.out.println(treeObject.getEntries().iterator().next().dump());
		System.out.println(treeObject.getEntry("README").dump());
		System.out.println(treeObject.getEntry("lib").dump());
		System.out.println(treeObject.getEntry("lib").getEntry("greeter.rb").dump());
	}
}
