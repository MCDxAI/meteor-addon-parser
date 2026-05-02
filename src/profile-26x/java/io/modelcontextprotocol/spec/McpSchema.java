package io.modelcontextprotocol.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public final class McpSchema {
  private McpSchema() {}

  public interface Content {}

  public interface ResourceContents {}

  public record TextContent(String type, String text, Map<String, Object> annotations)
      implements Content {
    public TextContent(String text) {
      this("text", text, null);
    }
  }

  public record TextResourceContents(String uri, String mimeType, String text)
      implements ResourceContents {}

  public record CallToolRequest(Map<String, Object> arguments) {
    public CallToolRequest() {
      this(Map.of());
    }
  }

  public record ReadResourceRequest(String uri) {
    public ReadResourceRequest() {
      this("");
    }
  }

  public record ReadResourceResult(List<ResourceContents> contents) {}

  public record CallToolResult(
      List<Content> content, Boolean isError, Object structuredContent) {
    public CallToolResult(List<Content> content, Boolean isError) {
      this(content, isError, null);
    }

    public CallToolResult(String content, Boolean isError) {
      this(List.of(new TextContent(content)), isError, null);
    }

    public static Builder builder() {
      return new Builder();
    }

    public static final class Builder {
      private final List<Content> content = new ArrayList<>();
      private Boolean isError;
      private Object structuredContent;

      public Builder addTextContent(String text) {
        content.add(new TextContent(text));
        return this;
      }

      public Builder isError(Boolean isError) {
        this.isError = isError;
        return this;
      }

      public Builder structuredContent(Object structuredContent) {
        this.structuredContent = structuredContent;
        return this;
      }

      public CallToolResult build() {
        return new CallToolResult(List.copyOf(content), isError, structuredContent);
      }
    }
  }

  public record JsonSchema(
      String type,
      Map<String, Object> properties,
      List<String> required,
      Boolean additionalProperties,
      Map<String, Object> definitions,
      Map<String, Object> schema) {
    public JsonSchema() {
      this(null, Map.of(), List.of(), null, Map.of(), Map.of());
    }
  }

  public record Tool(String name, String description, JsonSchema inputSchema) {
    public static Builder builder() {
      return new Builder();
    }

    public static final class Builder {
      private String name;
      private String description;
      private JsonSchema inputSchema;

      public Builder name(String name) {
        this.name = name;
        return this;
      }

      public Builder description(String description) {
        this.description = description;
        return this;
      }

      public Builder inputSchema(JsonSchema inputSchema) {
        this.inputSchema = inputSchema;
        return this;
      }

      public Tool build() {
        return new Tool(name, description, inputSchema);
      }
    }
  }

  public record Resource(String uri, String name, String description, String mimeType) {
    public static Builder builder() {
      return new Builder();
    }

    public static final class Builder {
      private String uri;
      private String name;
      private String description;
      private String mimeType;

      public Builder uri(String uri) {
        this.uri = uri;
        return this;
      }

      public Builder name(String name) {
        this.name = name;
        return this;
      }

      public Builder description(String description) {
        this.description = description;
        return this;
      }

      public Builder mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
      }

      public Resource build() {
        return new Resource(uri, name, description, mimeType);
      }
    }
  }
}
