# ether-http-client

Lightweight HTTP client utilities built on top of `java.net.http` and `ether-json`.

## Scope

- Immutable request and response models
- Configurable `HttpClient` wrapper with sane defaults
- JSON request/response helpers powered by `ether-json`
- Header and body normalization for reusable integrations

## Notes

- Built on top of `java.net.http`.
- Intended as the outbound counterpart to `ether-http-core`.
- Keeps transport concerns explicit instead of hiding them behind framework abstractions.

## Maven

```xml
<dependency>
  <groupId>dev.rafex.ether.http</groupId>
  <artifactId>ether-http-client</artifactId>
  <version>8.0.0-SNAPSHOT</version>
</dependency>
```

## Example

```java
var client = DefaultEtherHttpClient.create(JsonUtils.codec());
var response = client.send(HttpRequestSpec.get(URI.create("https://example.com")).build());
```
