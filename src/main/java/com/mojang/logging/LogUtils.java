package com.mojang.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogUtils {
  private LogUtils() {}

  public static Logger getLogger() {
    return LoggerFactory.getLogger("Mojang");
  }
}
