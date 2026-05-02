package net.minecraft.util;

import java.util.Random;

@SuppressWarnings("all")
public class RandomSource extends Random {
  public static RandomSource create() {
    return new RandomSource();
  }
}
