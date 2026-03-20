package dev.rafex.ether.http.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dev.rafex.ether.http.client.impl.DefaultEtherHttpClient;
import dev.rafex.ether.http.client.model.HttpRequestSpec;
import dev.rafex.ether.json.JsonUtils;

class DefaultEtherHttpClientTest {

    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/echo", this::handleEcho);
        server.start();
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldSendJsonAndParseResponse() throws Exception {
        final var client = DefaultEtherHttpClient.create();
        final var uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/echo");
        final var response = client.sendJson(HttpRequestSpec.post(uri).jsonBody(Map.of("name", "ether")).build(),
                new TypeReference<Map<String, Object>>() {
                });

        assertEquals("ether", response.get("received"));
        assertTrue(((Number) response.get("size")).intValue() > 0);
    }

    @Test
    void requestBuilderShouldSetJsonHeaders() {
        final var spec = HttpRequestSpec.post(URI.create("http://localhost/test")).jsonBody(Map.of("ok", true)).build();

        assertEquals("POST", spec.method().verb());
        assertEquals("application/json", spec.headers().get("Content-Type").get(0));
        assertTrue(new String(spec.body(), StandardCharsets.UTF_8).contains("\"ok\":true"));
    }

    private void handleEcho(final HttpExchange exchange) throws IOException {
        final var body = exchange.getRequestBody().readAllBytes();
        final var payload = Map.of("received", "ether", "size", body.length, "method", exchange.getRequestMethod(),
                "headers", exchange.getRequestHeaders().getOrDefault("Content-Type", List.of()));
        final byte[] response = JsonUtils.toJsonBytes(payload);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
