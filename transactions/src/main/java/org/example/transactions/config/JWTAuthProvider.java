/* Licensed under Apache-2.0 2024. */
package org.example.transactions.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import jakarta.inject.Singleton;

@Module
final class JWTAuthProvider {

  private JWTAuthProvider() {}

  @Singleton
  @Provides
  static JWTAuth jwtAuth(Vertx vertx, TransactionsConfig transactionsConfig) {
    return JWTAuth.create(
        vertx,
        new JWTAuthOptions()
            .addPubSecKey(
                new PubSecKeyOptions()
                    .setAlgorithm("HS256")
                    .setId("simple")
                    .setBuffer(transactionsConfig.jwt().secret())));
  }
}
