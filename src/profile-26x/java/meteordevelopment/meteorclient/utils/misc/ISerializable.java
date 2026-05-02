package meteordevelopment.meteorclient.utils.misc;

public interface ISerializable<T> {
  net.minecraft.nbt.CompoundTag toTag();

  T fromTag(net.minecraft.nbt.CompoundTag tag);
}
