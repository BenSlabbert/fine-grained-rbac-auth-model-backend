/* Licensed under Apache-2.0 2024. */
package org.example.app.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Module
final class SessionStoreProvider {

  private static final Logger log = LoggerFactory.getLogger(SessionStoreProvider.class);

  private SessionStoreProvider() {}

  @Singleton
  @Provides
  static SessionStore sessionStore(Vertx vertx) {
    if (vertx.isClustered()) {
      log.info("using clustered session store");
      return ClusteredSessionStore.create(vertx);
    }

    log.info("using local session store");
    return LocalSessionStore.create(vertx);
  }
}
