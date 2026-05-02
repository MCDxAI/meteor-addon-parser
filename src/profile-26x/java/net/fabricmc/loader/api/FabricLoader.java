package net.fabricmc.loader.api;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("all")
public class FabricLoader {
  private static final FabricLoader INSTANCE = new FabricLoader();

  public static FabricLoader getInstance() {
    return INSTANCE;
  }

  public List<ModContainer> getAllMods() {
    return List.of();
  }

  public <T> List<EntrypointContainer<T>> getEntrypointContainers(String key, Class<T> type) {
    return List.of();
  }

  public Path getGameDir() {
    return Path.of(System.getProperty("addonparser.meteorFolder", "."));
  }

  public boolean isModLoaded(String id) {
    return false;
  }

  public Optional<ModContainer> getModContainer(String id) {
    return Optional.empty();
  }

  public MappingResolver getMappingResolver() {
    return new MappingResolver();
  }
}
