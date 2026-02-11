package com.cope.addonparser.model;

import java.util.ArrayList;
import java.util.List;

public class ModuleDump {
  public String category;
  public String name;
  public String title;
  public String description;
  public String addonPackage;
  public String className;
  public List<String> aliases = new ArrayList<>();
  public List<SettingGroupDump> groups = new ArrayList<>();
}
