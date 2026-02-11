package com.cope.addonparser.util;

import com.cope.addonparser.model.FabricModMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FabricModParser {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private FabricModParser() {}

  public static FabricModMetadata parse(Path jarPath) throws IOException {
    try (ZipFile zip = new ZipFile(jarPath.toFile())) {
      ZipEntry entry = zip.getEntry("fabric.mod.json");
      if (entry == null) {
        throw new IOException("Missing fabric.mod.json in " + jarPath);
      }

      try (InputStream in = zip.getInputStream(entry)) {
        JsonNode root = MAPPER.readTree(in);
        FabricModMetadata meta = new FabricModMetadata();

        meta.id = text(root.get("id"));
        meta.name = text(root.get("name"));
        meta.version = text(root.get("version"));

        JsonNode authors = root.get("authors");
        if (authors != null && authors.isArray()) {
          for (JsonNode author : authors) {
            if (author.isTextual()) {
              meta.authors.add(author.asText());
            } else if (author.isObject()) {
              String n = text(author.get("name"));
              if (n != null) meta.authors.add(n);
            }
          }
        }

        JsonNode custom = root.get("custom");
        if (custom != null && custom.isObject()) {
          JsonNode colorNode = custom.get("meteor-client:color");
          if (colorNode != null && colorNode.isTextual()) meta.color = colorNode.asText();
        }

        JsonNode eps = root.get("entrypoints");
        if (eps != null && eps.isObject()) {
          JsonNode meteor = eps.get("meteor");
          if (meteor != null && meteor.isArray()) {
            for (JsonNode ep : meteor) {
              if (ep.isTextual()) {
                meta.meteorEntrypoints.add(ep.asText());
              } else if (ep.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = ep.fields();
                while (fields.hasNext()) {
                  Map.Entry<String, JsonNode> f = fields.next();
                  if (f.getValue().isTextual()) {
                    meta.meteorEntrypoints.add(f.getValue().asText());
                  }
                }
              }
            }
          }
        }

        return meta;
      }
    }
  }

  private static String text(JsonNode node) {
    return node == null || node.isNull() ? null : node.asText();
  }
}
