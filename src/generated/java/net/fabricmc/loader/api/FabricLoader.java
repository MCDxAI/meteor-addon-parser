// AUTO-GENERATED FILE. DO NOT EDIT.
package net.fabricmc.loader.api;

@SuppressWarnings({"all", "unchecked"})
public interface FabricLoader {
    default java.util.Collection getAllMods() {
        return (java.util.Collection) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Collection.class);
    }

    default java.util.List getEntrypointContainers(java.lang.String p0, java.lang.Class p1) {
        return (java.util.List) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.List.class);
    }

    default java.nio.file.Path getGameDir() {
        return (java.nio.file.Path) com.cope.addonparser.stubs.StubSupport.defaultValue(java.nio.file.Path.class);
    }

    static net.fabricmc.loader.api.FabricLoader getInstance() {
        return (net.fabricmc.loader.api.FabricLoader) com.cope.addonparser.stubs.StubSupport.defaultValue(net.fabricmc.loader.api.FabricLoader.class);
    }

    default java.util.Optional getModContainer(java.lang.String p0) {
        return (java.util.Optional) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Optional.class);
    }

    default boolean isModLoaded(java.lang.String p0) {
        return false;
    }

}
