package dev.rafex.ether.http.client;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.core.type.TypeReference;

public interface EtherHttpClient extends AutoCloseable {

	HttpResponseSpec send(HttpRequestSpec request) throws IOException, InterruptedException;

	default HttpResponseSpec get(final URI uri) throws IOException, InterruptedException {
		return send(HttpRequestSpec.get(uri).build());
	}

	<T> T sendJson(HttpRequestSpec request, TypeReference<T> typeReference) throws IOException, InterruptedException;

	<T> T sendJson(HttpRequestSpec request, Class<T> type) throws IOException, InterruptedException;

	@Override
	default void close() throws Exception {
	}
}
