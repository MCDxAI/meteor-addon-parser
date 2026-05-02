package org.apache.catalina;

@SuppressWarnings("all")
public interface Context extends Container {
  Wrapper createWrapper();

  void addChild(Container child);

  void addServletMappingDecoded(String pattern, String name);
}
