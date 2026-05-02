package net.fabricmc.loader.api;

@SuppressWarnings("all")
public class MappingResolver {
  public java.util.Collection<String> getNamespaces() {
    return java.util.List.of("mojmap", "named", "intermediary");
  }

  public String getCurrentRuntimeNamespace() {
    return "mojmap";
  }

  public String mapClassName(String namespace, String className) {
    return className;
  }

  public String mapFieldName(String namespace, String owner, String name, String descriptor) {
    return name;
  }

  public String mapMethodName(String namespace, String owner, String name, String descriptor) {
    return name;
  }
}
