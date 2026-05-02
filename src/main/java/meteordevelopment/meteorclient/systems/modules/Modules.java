package meteordevelopment.meteorclient.systems.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Modules {
  private static final Modules INSTANCE = new Modules();
  private static final List<Category> CATEGORIES = new ArrayList<>();

  private static boolean strictCategoryRegistration = false;
  private static boolean strictCategoryCheck = false;

  private final Map<Class<? extends Module>, Module> moduleInstances = new HashMap<>();
  private final Map<String, Module> placeholderInstances = new HashMap<>();
  private final Map<Category, List<Module>> groups = new HashMap<>();
  private final List<Module> active = new ArrayList<>();
  private final Category placeholderCategory = new Category("Meteor");

  private Modules() {}

  public static Modules get() {
    return INSTANCE;
  }

  public static void setStrictCategoryRegistration(boolean strict) {
    strictCategoryRegistration = strict;
  }

  public static void setStrictCategoryCheck(boolean strict) {
    strictCategoryCheck = strict;
  }

  public static void registerCategory(Category category) {
    if (category == null) return;
    if (strictCategoryRegistration && !Categories.REGISTERING) {
      throw new RuntimeException(
          "Modules.registerCategory - outside onRegisterCategories callback.");
    }
    if (!CATEGORIES.contains(category)) CATEGORIES.add(category);
  }

  public static Iterable<Category> loopCategories() {
    return CATEGORIES;
  }

  @SuppressWarnings("unchecked")
  public <T extends Module> T get(Class<T> klass) {
    return (T) moduleInstances.get(klass);
  }

  public <T extends Module> Optional<T> getOptional(Class<T> klass) {
    return Optional.ofNullable(get(klass));
  }

  public Module get(String name) {
    if (name == null) return null;
    for (Module module : moduleInstances.values()) {
      if (name.equalsIgnoreCase(module.name)) return module;
    }

    String key = name.toLowerCase();
    Module placeholder = placeholderInstances.get(key);
    if (placeholder != null) return placeholder;

    placeholder = new Module(placeholderCategory, name, "") {};
    placeholderInstances.put(key, placeholder);
    getGroup(placeholderCategory).add(placeholder);
    return placeholder;
  }

  public boolean isActive(Class<? extends Module> klass) {
    Module module = get(klass);
    return module != null && module.isActive();
  }

  public List<Module> getGroup(Category category) {
    return groups.computeIfAbsent(category, ignored -> new ArrayList<>());
  }

  public Collection<Module> getAll() {
    return moduleInstances.values();
  }

  public int getCount() {
    return moduleInstances.size();
  }

  public List<Module> getActive() {
    return active;
  }

  void addActive(Module module) {
    if (module != null && !active.contains(module)) active.add(module);
  }

  void removeActive(Module module) {
    active.remove(module);
  }

  public void add(Module module) {
    if (module == null) return;

    if (!CATEGORIES.contains(module.category)) {
      if (strictCategoryCheck) throw new RuntimeException("Module category was not registered.");
      registerCategory(module.category);
    }

    purgeReplacedModules(module);
    moduleInstances.put(module.getClass(), module);
    List<Module> group = getGroup(module.category);
    if (!group.contains(module)) group.add(module);

    module.settings.registerColorSettings(module);
  }

  private void purgeReplacedModules(Module module) {
    Set<Module> replaced = new LinkedHashSet<>();

    Module existingByClass = moduleInstances.get(module.getClass());
    if (existingByClass != null) replaced.add(existingByClass);

    for (Module existing : moduleInstances.values()) {
      if (Objects.equals(existing.name, module.name)) replaced.add(existing);
    }

    for (Module existing : replaced) {
      moduleInstances.entrySet().removeIf(entry -> entry.getValue() == existing);

      List<Module> group = groups.get(existing.category);
      if (group != null) group.remove(existing);

      active.remove(existing);
    }
  }

  public static void reset() {
    INSTANCE.moduleInstances.clear();
    INSTANCE.groups.clear();
    INSTANCE.active.clear();
    CATEGORIES.clear();
    strictCategoryRegistration = false;
    strictCategoryCheck = false;
  }
}
