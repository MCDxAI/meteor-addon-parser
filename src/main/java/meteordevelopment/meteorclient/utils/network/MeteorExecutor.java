package meteordevelopment.meteorclient.utils.network;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@SuppressWarnings("all")
public final class MeteorExecutor {
  private MeteorExecutor() {}

  public static void execute(Runnable task) {}

  public static Future<?> submit(Runnable task) {
    return CompletableFuture.completedFuture(null);
  }
}
