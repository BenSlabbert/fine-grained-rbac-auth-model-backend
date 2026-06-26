/* Licensed under Apache-2.0 2026. */
package org.example.gateway.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.http.CookieSameSite;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import jakarta.inject.Singleton;
import java.time.Duration;

@Module
final class SessionHandlerProvider {

  private SessionHandlerProvider() {}

  @Singleton
  @Provides
  static SessionHandler getSessionHandler(SessionStore sessionStore) {
    Duration duration = Duration.ofMinutes(5L);
    return SessionHandler.create(sessionStore)
        .setCookieless(false)
        .setCookieHttpOnlyFlag(true)
        .setCookieSecureFlag(true)
        .setLazySession(true)
        .setCookieSameSite(CookieSameSite.STRICT)
        .setSessionTimeout(Duration.ofMinutes(5L).toMillis())
        .setSigningSecret("cookie-secret")
        .setSessionCookieName("vertx-session")
        .setSessionCookiePath("/")
        .setSessionTimeout(duration.toMillis())
        .setCookieMaxAge(duration.toSeconds())
        .setMinLength(128);
  }
}
