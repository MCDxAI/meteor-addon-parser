// AUTO-GENERATED FILE. DO NOT EDIT.
package baritone.api.behavior;

@SuppressWarnings({"all", "unchecked"})
public interface IPathingBehavior {
    default boolean cancelEverything() {
        return false;
    }

    default java.util.Optional estimatedTicksToGoal() {
        return (java.util.Optional) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Optional.class);
    }

    default void forceCancel() {
    }

    default java.util.Optional getPath() {
        return (java.util.Optional) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Optional.class);
    }

    default boolean hasPath() {
        return false;
    }

    default boolean isPathing() {
        return false;
    }

    default java.util.Optional ticksRemainingInSegment() {
        return (java.util.Optional) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Optional.class);
    }

}
