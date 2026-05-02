package meteordevelopment.meteorclient.utils.misc;

public enum HorizontalDirection {
  South("South", "Z+", false, 0, 0, 1),
  SouthEast("South East", "X+ Z+", true, -45, 1, 1),
  West("West", "X-", false, 90, -1, 0),
  NorthWest("North West", "X- Z-", true, 135, -1, -1),
  North("North", "Z-", false, 180, 0, -1),
  NorthEast("North East", "X+ Z-", true, -135, 1, -1),
  East("East", "X+", false, -90, 1, 0),
  SouthWest("South West", "X- Z+", true, 45, -1, 1);

  public final String name;
  public final String axis;
  public final boolean diagonal;
  public final float yaw;
  public final int offsetX, offsetZ;

  HorizontalDirection(
      String name, String axis, boolean diagonal, float yaw, int offsetX, int offsetZ) {
    this.axis = axis;
    this.name = name;
    this.diagonal = diagonal;
    this.yaw = yaw;
    this.offsetX = offsetX;
    this.offsetZ = offsetZ;
  }

  public static HorizontalDirection get(float yaw) {
    yaw = yaw % 360;
    if (yaw < 0) yaw += 360;
    if (yaw >= 337.5 || yaw < 22.5) return South;
    if (yaw < 67.5) return SouthWest;
    if (yaw < 112.5) return West;
    if (yaw < 157.5) return NorthWest;
    if (yaw < 202.5) return North;
    if (yaw < 247.5) return NorthEast;
    if (yaw < 292.5) return East;
    return SouthEast;
  }
}
