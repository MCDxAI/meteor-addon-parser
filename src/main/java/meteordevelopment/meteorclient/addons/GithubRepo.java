package meteordevelopment.meteorclient.addons;

public class GithubRepo {
  private final String owner;
  private final String name;
  private final String branch;
  private final String commit;

  public GithubRepo(String owner, String name) {
    this(owner, name, null, null);
  }

  public GithubRepo(String owner, String name, String branch, String commit) {
    this.owner = owner;
    this.name = name;
    this.branch = branch;
    this.commit = commit;
  }

  public String owner() {
    return owner;
  }

  public String name() {
    return name;
  }

  public String branch() {
    return branch;
  }

  public String commit() {
    return commit;
  }
}
