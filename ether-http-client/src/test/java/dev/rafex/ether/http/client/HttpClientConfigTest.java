package dev.rafex.ether.http.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.http.HttpClient;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import dev.rafex.ether.http.client.config.HttpClientConfig;

class HttpClientConfigTest {

    @Test
    void defaultsShouldBeReasonable() {
        final var config = HttpClientConfig.defaults();

        assertEquals(Duration.ofSeconds(10), config.connectTimeout());
        assertEquals(Duration.ofSeconds(30), config.requestTimeout());
        assertEquals(HttpClient.Redirect.NORMAL, config.redirectPolicy());
        assertFalse(config.defaultHeaders().containsKey("X-Test"));
    }
}
