package com.cope.addonparser;

import static org.junit.jupiter.api.Assertions.*;

import com.cope.addonparser.util.NameUtils;
import org.junit.jupiter.api.Test;

/** AP-007: Unit tests for shared nameToTitle utility. */
public class NameUtilsTest {

  @Test
  void kebabCase() {
    assertEquals("Auto Jump", NameUtils.nameToTitle("auto-jump"));
  }

  @Test
  void snakeCase() {
    assertEquals("Render Mode", NameUtils.nameToTitle("render_mode"));
  }

  @Test
  void mixedCase() {
    assertEquals("ElytraFly", NameUtils.nameToTitle("ElytraFly"));
  }

  @Test
  void singleWord() {
    assertEquals("Sprint", NameUtils.nameToTitle("sprint"));
  }

  @Test
  void multipleDashes() {
    assertEquals("Long Name Here", NameUtils.nameToTitle("long-name-here"));
  }

  @Test
  void nullInput() {
    assertEquals("", NameUtils.nameToTitle(null));
  }

  @Test
  void emptyInput() {
    assertEquals("", NameUtils.nameToTitle(""));
  }

  @Test
  void mixedSeparators() {
    assertEquals("Foo Bar Baz", NameUtils.nameToTitle("foo-bar_baz"));
  }
}
