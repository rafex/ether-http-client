package dev.rafex.ether.http.client;

public enum HttpMethod {
	GET,
	POST,
	PUT,
	PATCH,
	DELETE,
	HEAD,
	OPTIONS;

	public String verb() {
		return name();
	}
}
