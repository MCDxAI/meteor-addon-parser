package io.modelcontextprotocol.server;

import io.modelcontextprotocol.spec.McpStreamableServerTransportProvider;
import java.time.Duration;
import java.util.List;

@SuppressWarnings("all")
public final class McpServer {
  private McpServer() {}

  public static StreamableSyncSpecification sync(McpStreamableServerTransportProvider transportProvider) {
    return new StreamableSyncSpecification();
  }

  public static SyncSpecification sync(Object transportProvider) {
    return new SyncSpecification();
  }

  public static AsyncSpec async(Object transportProvider) {
    return new AsyncSpec();
  }

  public static class StreamableSyncSpecification extends SyncSpecification {}

  public static class SyncSpecification {
    public SyncSpecification serverInfo(String name, String version) {
      return this;
    }

    public SyncSpecification capabilities(Object capabilities) {
      return this;
    }

    public SyncSpecification instructions(String instructions) {
      return this;
    }

    public SyncSpecification tools(List<McpServerFeatures.SyncToolSpecification> tools) {
      return this;
    }

    public SyncSpecification resources(List<McpServerFeatures.SyncResourceSpecification> resources) {
      return this;
    }

    public SyncSpecification requestTimeout(Duration requestTimeout) {
      return this;
    }

    public McpSyncServer build() {
      return new McpSyncServer();
    }
  }

  public static class AsyncSpec {
    public AsyncSpec serverInfo(String name, String version) {
      return this;
    }

    public AsyncSpec capabilities(Object capabilities) {
      return this;
    }

    public Object build() {
      return new Object();
    }
  }
}
