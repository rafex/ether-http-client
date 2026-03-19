package dev.rafex.ether.http.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.net.http.HttpClient;

import org.junit.jupiter.api.Test;

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
