package com.cope.addonparser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AP-006: Verify module replacement removes stale entries from group lists. */
public class ModuleRegistryTest {

  @BeforeEach
  void reset() {
    Modules.reset();
  }

  @Test
  void replacingByNamePurgesOldGroupEntry() {
    Category cat = new Category("Combat");
    Modules.registerCategory(cat);

    Module original = new TestModule(cat, "TestMod", "Original");
    Modules.get().add(original);

    assertEquals(1, Modules.get().getGroup(cat).size());
    assertEquals(1, Modules.get().getCount());

    // Replace with a different class but same name
    Module replacement = new TestModule2(cat, "TestMod", "Replacement");
    Modules.get().add(replacement);

    List<Module> group = Modules.get().getGroup(cat);
    assertEquals(1, group.size(), "Group should contain exactly the replacement");
    assertSame(replacement, group.get(0));
    assertEquals(1, Modules.get().getCount());
  }

  @Test
  void replacingAcrossCategoriesPurgesOldGroup() {
    Category combat = new Category("Combat");
    Category movement = new Category("Movement");
    Modules.registerCategory(combat);
    Modules.registerCategory(movement);

    Module original = new TestModule(combat, "TestMod", "Original");
    Modules.get().add(original);

    assertEquals(1, Modules.get().getGroup(combat).size());

    // Replace with same name but different category
    Module replacement = new TestModule2(movement, "TestMod", "Replacement");
    Modules.get().add(replacement);

    assertEquals(0, Modules.get().getGroup(combat).size(), "Old category group should be empty");
    assertEquals(
        1, Modules.get().getGroup(movement).size(), "New category group should have replacement");
    assertEquals(1, Modules.get().getCount());
  }

  @Test
  void replacingByClassPurgesOldGroupEntryEvenWhenNameChanges() {
    Category cat = new Category("Combat");
    Modules.registerCategory(cat);

    Module original = new TestModule(cat, "OldName", "Original");
    Modules.get().add(original);
    original.toggle();

    assertEquals(1, Modules.get().getGroup(cat).size());
    assertEquals(1, Modules.get().getActive().size());

    Module replacement = new TestModule(cat, "NewName", "Replacement");
    Modules.get().add(replacement);

    List<Module> group = Modules.get().getGroup(cat);
    assertEquals(1, group.size(), "Group should contain exactly the replacement");
    assertSame(replacement, group.get(0));
    assertEquals(
        0, Modules.get().getActive().size(), "Active list should not retain replaced module");
    assertEquals(1, Modules.get().getCount());
  }

  private static class TestModule extends Module {
    TestModule(Category category, String name, String description) {
      super(category, name, description);
    }
  }

  private static class TestModule2 extends Module {
    TestModule2(Category category, String name, String description) {
      super(category, name, description);
    }
  }
}
