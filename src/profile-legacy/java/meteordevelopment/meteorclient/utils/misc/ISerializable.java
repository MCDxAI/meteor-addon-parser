package meteordevelopment.meteorclient.utils.misc;

public interface ISerializable<T> {
  net.minecraft.class_2487 toTag();

  T fromTag(net.minecraft.class_2487 tag);
}
