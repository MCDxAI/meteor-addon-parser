package com.cope.addonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cope.addonparser.scanner.RuntimeMode;
import org.junit.jupiter.api.Test;

public class RuntimeModeTest {
  @Test
  void nullAndBlankDefaultToLegacy() {
    assertEquals(RuntimeMode.LEGACY, RuntimeMode.fromString(null));
    assertEquals(RuntimeMode.LEGACY, RuntimeMode.fromString(""));
    assertEquals(RuntimeMode.LEGACY, RuntimeMode.fromString("   "));
  }

  @Test
  void explicitModesAreParsed() {
    assertEquals(RuntimeMode.LEGACY, RuntimeMode.fromString("legacy"));
    assertEquals(RuntimeMode.LEGACY, RuntimeMode.fromString("in-process"));
    assertEquals(RuntimeMode.ISOLATED, RuntimeMode.fromString("isolated"));
  }
}
