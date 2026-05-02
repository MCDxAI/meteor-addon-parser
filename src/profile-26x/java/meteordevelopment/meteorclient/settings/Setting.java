package meteordevelopment.meteorclient.settings;

import com.cope.addonparser.util.NameUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.IGetter;
import meteordevelopment.meteorclient.utils.misc.ISerializable;

public abstract class Setting<T> implements IGetter<T>, ISerializable<T> {
  private static final List<String> NO_SUGGESTIONS = new ArrayList<>(0);

  public final String name;
  public final String title;
  public final String description;
  private final IVisible visible;

  protected final T defaultValue;
  protected T value;

  public final Consumer<Setting<T>> onModuleActivated;
  private final Consumer<T> onChanged;

  public Module module;
  public boolean lastWasVisible;

  public Setting(
      String name,
      String description,
      T defaultValue,
      Consumer<T> onChanged,
      Consumer<Setting<T>> onModuleActivated,
      IVisible visible) {
    this.name = name;
    this.title = NameUtils.nameToTitle(name);
    this.description = description;
    this.defaultValue = defaultValue;
    this.onChanged = onChanged;
    this.onModuleActivated = onModuleActivated;
    this.visible = visible;

    resetImpl();
  }

  @Override
  public T get() {
    return value;
  }

  public boolean set(T value) {
    if (!isValueValid(value)) return false;
    this.value = value;
    onChanged();
    return true;
  }

  protected void resetImpl() {
    value = defaultValue;
  }

  public void reset() {
    resetImpl();
    onChanged();
  }

  public T getDefaultValue() {
    return defaultValue;
  }

  public boolean parse(String str) {
    T newValue = parseImpl(str);
    if (newValue != null && isValueValid(newValue)) {
      value = newValue;
      onChanged();
    }
    return newValue != null;
  }

  public boolean wasChanged() {
    return !Objects.equals(value, defaultValue);
  }

  public void onChanged() {
    if (onChanged != null) onChanged.accept(value);
  }

  public void onActivated() {
    if (onModuleActivated != null) onModuleActivated.accept(this);
  }

  public boolean isVisible() {
    return visible == null || visible.isVisible();
  }

  protected abstract T parseImpl(String str);

  protected abstract boolean isValueValid(T value);

  public List<String> getSuggestions() {
    return NO_SUGGESTIONS;
  }

  protected abstract net.minecraft.class_2487 save(net.minecraft.class_2487 tag);

  @Override
  public net.minecraft.nbt.CompoundTag toTag() {
    return new net.minecraft.nbt.CompoundTag();
  }

  protected abstract T load(net.minecraft.class_2487 tag);

  @Override
  public T fromTag(net.minecraft.nbt.CompoundTag tag) {
    onChanged();
    return value;
  }

  public T fromTag(net.minecraft.class_2487 tag) {
    T loaded = load(tag);
    if (loaded != null) value = loaded;
    onChanged();
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @SuppressWarnings("unchecked")
  public abstract static class SettingBuilder<B, V, S> {
    protected String name = "undefined";
    protected String description = "";
    protected V defaultValue;
    protected IVisible visible;
    protected Consumer<V> onChanged;
    protected Consumer<Setting<V>> onModuleActivated;

    protected SettingBuilder(V defaultValue) {
      this.defaultValue = defaultValue;
    }

    public B name(String name) {
      this.name = name;
      return (B) this;
    }

    public B description(String description) {
      this.description = description;
      return (B) this;
    }

    public B defaultValue(V defaultValue) {
      this.defaultValue = defaultValue;
      return (B) this;
    }

    public B visible(IVisible visible) {
      this.visible = visible;
      return (B) this;
    }

    public B onChanged(java.util.function.Consumer<V> onChanged) {
      this.onChanged = onChanged;
      return (B) this;
    }

    public B onModuleActivated(java.util.function.Consumer<Setting<V>> onModuleActivated) {
      this.onModuleActivated = onModuleActivated;
      return (B) this;
    }

    public abstract S build();
  }
}
