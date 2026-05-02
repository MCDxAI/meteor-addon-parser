package net.minecraft.server.packs.resources;

@SuppressWarnings("all")
public interface ResourceManagerReloadListener {
  default void onResourceManagerReload(ResourceManager resourceManager) {}
}
