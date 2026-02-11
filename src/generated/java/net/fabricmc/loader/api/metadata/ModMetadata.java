// AUTO-GENERATED FILE. DO NOT EDIT.
package net.fabricmc.loader.api.metadata;

@SuppressWarnings({"all", "unchecked"})
public interface ModMetadata {
    default java.util.Collection getAuthors() {
        return (java.util.Collection) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Collection.class);
    }

    default net.fabricmc.loader.api.metadata.ContactInformation getContact() {
        return (net.fabricmc.loader.api.metadata.ContactInformation) com.cope.addonparser.stubs.StubSupport.defaultValue(net.fabricmc.loader.api.metadata.ContactInformation.class);
    }

    default net.fabricmc.loader.api.metadata.CustomValue getCustomValue(java.lang.String p0) {
        return (net.fabricmc.loader.api.metadata.CustomValue) com.cope.addonparser.stubs.StubSupport.defaultValue(net.fabricmc.loader.api.metadata.CustomValue.class);
    }

    default java.lang.String getDescription() {
        return (java.lang.String) com.cope.addonparser.stubs.StubSupport.defaultValue(java.lang.String.class);
    }

    default java.util.Optional getIconPath(int p0) {
        return (java.util.Optional) com.cope.addonparser.stubs.StubSupport.defaultValue(java.util.Optional.class);
    }

    default java.lang.String getId() {
        return (java.lang.String) com.cope.addonparser.stubs.StubSupport.defaultValue(java.lang.String.class);
    }

    default java.lang.String getName() {
        return (java.lang.String) com.cope.addonparser.stubs.StubSupport.defaultValue(java.lang.String.class);
    }

    default net.fabricmc.loader.api.Version getVersion() {
        return (net.fabricmc.loader.api.Version) com.cope.addonparser.stubs.StubSupport.defaultValue(net.fabricmc.loader.api.Version.class);
    }

}
