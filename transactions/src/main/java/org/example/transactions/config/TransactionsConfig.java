/* Licensed under Apache-2.0 2026. */
package org.example.transactions.config;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nonnull;

@GenerateBuilder
public record TransactionsConfig(@Nonnull Jwt jwt) {

  public static final String APP_NAME = "transactions";

  @GenerateBuilder
  public record Jwt(Buffer secret) {}

  public static TransactionsConfig create(JsonObject json) {

    return TransactionsConfigBuilder.builder().jwt(getJwt(json.getJsonObject("jwt"))).build();
  }

  private static Jwt getJwt(JsonObject json) {
    return TransactionsConfig_JwtBuilder.builder().secret(json.getBuffer("secret")).build();
  }
}
