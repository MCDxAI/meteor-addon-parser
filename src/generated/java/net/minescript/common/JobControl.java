// AUTO-GENERATED FILE. DO NOT EDIT.
package net.minescript.common;

@SuppressWarnings({"all", "unchecked"})
public interface JobControl {
    default java.lang.String jobSummary() {
        return (java.lang.String) com.cope.addonparser.stubs.StubSupport.defaultValue(java.lang.String.class);
    }

    default void requestKill() {
    }

    default boolean resume() {
        return false;
    }

    default net.minescript.common.JobState state() {
        return (net.minescript.common.JobState) com.cope.addonparser.stubs.StubSupport.defaultValue(net.minescript.common.JobState.class);
    }

    default boolean suspend() {
        return false;
    }

}
