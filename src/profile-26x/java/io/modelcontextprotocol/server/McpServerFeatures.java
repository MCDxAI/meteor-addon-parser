package io.modelcontextprotocol.server;

import io.modelcontextprotocol.spec.McpSchema;
import java.util.function.BiFunction;

@SuppressWarnings("all")
public final class McpServerFeatures {
  private McpServerFeatures() {}

  public record SyncToolSpecification(
      McpSchema.Tool tool,
      BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult>
          callHandler) {
    public static Builder builder() {
      return new Builder();
    }

    public static final class Builder {
      private McpSchema.Tool tool;
      private BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult>
          callHandler;

      public Builder tool(McpSchema.Tool tool) {
        this.tool = tool;
        return this;
      }

      public Builder callHandler(
          BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult>
              callHandler) {
        this.callHandler = callHandler;
        return this;
      }

      public SyncToolSpecification build() {
        return new SyncToolSpecification(tool, callHandler);
      }
    }
  }

  public record SyncResourceSpecification(
      McpSchema.Resource resource,
      BiFunction<McpSyncServerExchange, McpSchema.ReadResourceRequest, McpSchema.ReadResourceResult>
          readHandler) {}
}
