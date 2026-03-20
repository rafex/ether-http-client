package dev.rafex.ether.http.client.config;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record HttpClientConfig(Duration connectTimeout, Duration requestTimeout, HttpClient.Redirect redirectPolicy,
        String userAgent, Map<String, List<String>> defaultHeaders) {

    public static Builder builder() {
        return new Builder();
    }

    public static HttpClientConfig defaults() {
        return builder().build();
    }

    public Map<String, List<String>> defaultHeaders() {
        return Map.copyOf(defaultHeaders);
    }

    public static final class Builder {
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(30);
        private HttpClient.Redirect redirectPolicy = HttpClient.Redirect.NORMAL;
        private String userAgent = "ether-http-client/1.0";
        private final Map<String, List<String>> defaultHeaders = new LinkedHashMap<>();

        public Builder connectTimeout(final Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder requestTimeout(final Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder redirectPolicy(final HttpClient.Redirect redirectPolicy) {
            this.redirectPolicy = redirectPolicy;
            return this;
        }

        public Builder userAgent(final String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder defaultHeader(final String name, final String value) {
            defaultHeaders.computeIfAbsent(name, key -> new java.util.ArrayList<>()).add(value);
            return this;
        }

        public HttpClientConfig build() {
            return new HttpClientConfig(connectTimeout, requestTimeout, redirectPolicy, userAgent,
                    defaultHeaders.entrySet().stream().collect(java.util.stream.Collectors.toMap(Map.Entry::getKey,
                            entry -> List.copyOf(entry.getValue()), (left, right) -> left, LinkedHashMap::new)));
        }
    }
}
