package com.cope.addonparser.model;

import java.util.ArrayList;
import java.util.List;

public class JarScanResult {
  public String jarName;
  public String jarPath;
  public boolean success;
  public List<String> errors = new ArrayList<>();
  public List<String> warnings = new ArrayList<>();
  public List<AddonDump> addons = new ArrayList<>();
  public List<ModuleDump> modules = new ArrayList<>();
}
