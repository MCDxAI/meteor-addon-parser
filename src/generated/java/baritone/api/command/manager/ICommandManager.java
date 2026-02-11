// AUTO-GENERATED FILE. DO NOT EDIT.
package baritone.api.command.manager;

@SuppressWarnings({"all", "unchecked"})
public interface ICommandManager {
    default boolean execute(java.lang.String p0) {
        return false;
    }

    default baritone.api.command.registry.Registry getRegistry() {
        return (baritone.api.command.registry.Registry) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.command.registry.Registry.class);
    }

}
