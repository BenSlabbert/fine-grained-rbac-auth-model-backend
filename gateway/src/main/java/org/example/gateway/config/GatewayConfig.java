/* Licensed under Apache-2.0 2026. */
package org.example.gateway.config;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nonnull;
import java.util.List;

@GenerateBuilder
public record GatewayConfig(@Nonnull Jwt jwt, @Nonnull List<Service> services) {

  @GenerateBuilder
  public record Jwt(Buffer secret) {}

  @GenerateBuilder
  public record Service(String name, String host, int port) {}

  public static GatewayConfig create(JsonObject json) {

    return GatewayConfigBuilder.builder()
        .jwt(getJwt(json.getJsonObject("jwt")))
        .services(getServices(json.getJsonArray("services")))
        .build();
  }

  private static Jwt getJwt(JsonObject json) {
    return GatewayConfig_JwtBuilder.builder().secret(json.getBuffer("secret")).build();
  }

  private static List<Service> getServices(JsonArray jsonArray) {
    return jsonArray.stream()
        .map(j -> (JsonObject) j)
        .map(
            s ->
                GatewayConfig_ServiceBuilder.builder()
                    .name(s.getString("name"))
                    .host(s.getString("host"))
                    .port(s.getInteger("port"))
                    .build())
        .toList();
  }
}
