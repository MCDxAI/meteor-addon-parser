// AUTO-GENERATED FILE. DO NOT EDIT.
package com.mojang.serialization;

@SuppressWarnings({"all", "unchecked"})
public interface DataResult {
    default java.lang.Object getOrThrow() {
        return (java.lang.Object) com.cope.addonparser.stubs.StubSupport.defaultValue(java.lang.Object.class);
    }

    default com.mojang.serialization.DataResult ifError(java.util.function.Consumer p0) {
        return (com.mojang.serialization.DataResult) com.cope.addonparser.stubs.StubSupport.defaultValue(com.mojang.serialization.DataResult.class);
    }

    default java.util.Optional result() {
        return (java.util.Optional) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Optional.class);
    }

}
