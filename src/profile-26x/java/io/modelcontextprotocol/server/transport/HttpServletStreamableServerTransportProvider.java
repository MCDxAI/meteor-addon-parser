package io.modelcontextprotocol.server.transport;

@SuppressWarnings("all")
public class HttpServletStreamableServerTransportProvider
    implements io.modelcontextprotocol.spec.McpStreamableServerTransportProvider,
        jakarta.servlet.Servlet {
  public HttpServletStreamableServerTransportProvider() {}

  public HttpServletStreamableServerTransportProvider(Object... args) {}

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public Builder() {}

    public Builder contextPath(String value) {
      return this;
    }

    public Builder endpoint(String value) {
      return this;
    }

    public Builder objectMapper(Object value) {
      return this;
    }

    public Builder mcpEndpoint(String value) {
      return this;
    }

    public Builder keepAliveInterval(java.time.Duration value) {
      return this;
    }

    public HttpServletStreamableServerTransportProvider build() {
      return new HttpServletStreamableServerTransportProvider();
    }
  }
}
