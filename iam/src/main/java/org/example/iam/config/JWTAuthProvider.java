/* Licensed under Apache-2.0 2024. */
package org.example.iam.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Module
final class JWTAuthProvider {

  private JWTAuthProvider() {}

  @Named("machine-jwt")
  @Singleton
  @Provides
  static JWTAuth machineJwtAuth(Vertx vertx) {
    return JWTAuth.create(
        vertx,
        new JWTAuthOptions()
            .addPubSecKey(
                new PubSecKeyOptions()
                    .setAlgorithm("HS256")
                    .setId("machine-jwt")
                    .setBuffer("secret")));
  }
}
