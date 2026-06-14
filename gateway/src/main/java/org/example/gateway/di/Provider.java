/* Licensed under Apache-2.0 2026. */
package org.example.gateway.di;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.gateway.config.ConfigModule;
import org.example.gateway.web.RouterFactory;
import org.example.gateway.web.ServerFactory;
import org.example.gateway.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Component(
    modules = {
      Provider.EagerModule.class,
      ConfigModule.class,
      WebModule.class,
    })
public interface Provider {

  Logger log = LoggerFactory.getLogger(Provider.class);

  RouterFactory routerFactory();

  ServerFactory serverFactory();

  @Nullable Void init();

  default void closeSilently() {}

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder vertx(Vertx vertx);

    @BindsInstance
    Builder appConfig(ApplicationConfig config);

    @BindsInstance
    Builder config(JsonObject config);

    Provider build();
  }

  @Module
  final class EagerModule {

    @Inject
    EagerModule() {}

    @Provides
    @Nullable static Void provideEager() {
      log.info("eager init");
      return null;
    }
  }
}
