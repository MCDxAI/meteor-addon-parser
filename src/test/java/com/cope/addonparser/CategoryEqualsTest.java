package com.cope.addonparser;

import static org.junit.jupiter.api.Assertions.*;

import meteordevelopment.meteorclient.systems.modules.Category;
import org.junit.jupiter.api.Test;

/** AP-003: Verify Category.equals uses name identity, not just hash. */
public class CategoryEqualsTest {

  @Test
  void equalCategoriesWithSameName() {
    Category a = new Category("Combat");
    Category b = new Category("Combat");
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void differentCategoriesNotEqual() {
    Category a = new Category("Combat");
    Category b = new Category("Movement");
    assertNotEquals(a, b);
  }

  @Test
  void hashCollisionDoesNotCauseFalseEquality() {
    // Find two strings with the same hashCode but different values
    // "Aa" and "BB" have the same hashCode in Java
    Category a = new Category("Aa");
    Category b = new Category("BB");
    assertEquals(a.hashCode(), b.hashCode(), "Test requires hash collision");
    assertNotEquals(a, b, "Different names with same hash must not be equal");
  }

  @Test
  void nullNameHandling() {
    Category a = new Category(null);
    Category b = new Category(null);
    assertEquals(a, b);

    Category c = new Category("Combat");
    assertNotEquals(a, c);
    assertNotEquals(c, a);
  }
}
