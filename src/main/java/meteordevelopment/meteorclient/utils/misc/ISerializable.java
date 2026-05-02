package meteordevelopment.meteorclient.utils.misc;

/**
 * Marker interface implemented by classes that round-trip to NBT in real Meteor. The parser does
 * not exercise serialization, so the interface intentionally has no methods - subclasses keep
 * their own profile-specific toTag/fromTag overloads as orphan methods.
 */
public interface ISerializable<T> {}
