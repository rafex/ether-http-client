package dev.rafex.ether.http.client.model;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record HttpResponseSpec(int statusCode, Map<String, List<String>> headers, byte[] body) {

    public HttpResponseSpec {
        headers = normalizeHeaders(headers);
        body = body == null ? new byte[0] : body.clone();
    }

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    public String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    private static Map<String, List<String>> normalizeHeaders(final Map<String, List<String>> raw) {
        if (raw == null || raw.isEmpty()) {
            return Map.of();
        }
        final var copy = new LinkedHashMap<String, List<String>>();
        for (final var entry : raw.entrySet()) {
            copy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(copy);
    }
}
