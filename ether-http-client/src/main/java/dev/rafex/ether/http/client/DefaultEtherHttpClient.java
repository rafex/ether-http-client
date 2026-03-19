package dev.rafex.ether.http.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.rafex.ether.json.JsonCodec;
import dev.rafex.ether.json.JsonUtils;

public final class DefaultEtherHttpClient implements EtherHttpClient {

	private final HttpClient httpClient;
	private final HttpClientConfig config;
	private final JsonCodec jsonCodec;

	public DefaultEtherHttpClient(final HttpClientConfig config) {
		this(config, JsonUtils.codec());
	}

	public DefaultEtherHttpClient(final HttpClientConfig config, final JsonCodec jsonCodec) {
		this.config = config == null ? HttpClientConfig.defaults() : config;
		this.jsonCodec = jsonCodec == null ? JsonUtils.codec() : jsonCodec;
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(this.config.connectTimeout())
				.followRedirects(this.config.redirectPolicy())
				.build();
	}

	public static DefaultEtherHttpClient create() {
		return new DefaultEtherHttpClient(HttpClientConfig.defaults());
	}

	public static DefaultEtherHttpClient create(final HttpClientConfig config) {
		return new DefaultEtherHttpClient(config);
	}

	public static DefaultEtherHttpClient create(final JsonCodec jsonCodec) {
		return new DefaultEtherHttpClient(HttpClientConfig.defaults(), jsonCodec);
	}

	@Override
	public HttpResponseSpec send(final HttpRequestSpec request) throws IOException, InterruptedException {
		final var response = httpClient.send(buildRequest(request), HttpResponse.BodyHandlers.ofByteArray());
		return new HttpResponseSpec(response.statusCode(), response.headers().map(), response.body());
	}

	@Override
	public <T> T sendJson(final HttpRequestSpec request, final TypeReference<T> typeReference)
			throws IOException, InterruptedException {
		final var response = send(request);
		return jsonCodec.readValue(response.body(), typeReference);
	}

	@Override
	public <T> T sendJson(final HttpRequestSpec request, final Class<T> type) throws IOException, InterruptedException {
		final var response = send(request);
		return jsonCodec.readValue(response.body(), type);
	}

	public <T> T sendJson(final HttpMethod method, final URI uri, final Object body, final Class<T> responseType)
			throws IOException, InterruptedException {
		return sendJson(HttpRequestSpec.builder(method, uri).jsonBody(body, jsonCodec).build(), responseType);
	}

	public <T> T sendJson(final HttpMethod method, final URI uri, final Object body, final TypeReference<T> responseType)
			throws IOException, InterruptedException {
		return sendJson(HttpRequestSpec.builder(method, uri).jsonBody(body, jsonCodec).build(), responseType);
	}

	public HttpResponseSpec sendText(final HttpMethod method, final URI uri, final String body)
			throws IOException, InterruptedException {
		return send(HttpRequestSpec.builder(method, uri).body(body).contentType("text/plain; charset=utf-8").build());
	}

	private HttpRequest buildRequest(final HttpRequestSpec request) {
		final var builder = HttpRequest.newBuilder(request.uri())
				.method(request.method().verb(), requestBodyPublisher(request))
				.timeout(request.timeout() != null ? request.timeout() : config.requestTimeout());

		applyHeaders(builder, config.defaultHeaders());
		applyHeaders(builder, request.headers());
		if (config.userAgent() != null && !config.userAgent().isBlank()) {
			builder.header("User-Agent", config.userAgent());
		}
		return builder.build();
	}

	private static void applyHeaders(final HttpRequest.Builder builder, final Map<String, List<String>> headers) {
		for (final var entry : headers.entrySet()) {
			for (final var value : entry.getValue()) {
				builder.header(entry.getKey(), value);
			}
		}
	}

	private static HttpRequest.BodyPublisher requestBodyPublisher(final HttpRequestSpec request) {
		final var body = request.body();
		if (body == null || body.length == 0) {
			return HttpRequest.BodyPublishers.noBody();
		}
		return HttpRequest.BodyPublishers.ofByteArray(body);
	}
}
