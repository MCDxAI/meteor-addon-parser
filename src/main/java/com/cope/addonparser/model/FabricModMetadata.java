package com.cope.addonparser.model;

import java.util.ArrayList;
import java.util.List;

public class FabricModMetadata {
  public String id;
  public String name;
  public String version;
  public String color;
  public List<String> authors = new ArrayList<>();
  public List<String> meteorEntrypoints = new ArrayList<>();
}
