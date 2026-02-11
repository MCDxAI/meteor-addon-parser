package com.cope.addonparser.model;

import java.util.ArrayList;
import java.util.List;

public class AddonDump {
  public String name;
  public String packageName;
  public String website;
  public String repoOwner;
  public String repoName;
  public String repoBranch;
  public String repoCommit;
  public List<String> authors = new ArrayList<>();
  public String entrypoint;
}
