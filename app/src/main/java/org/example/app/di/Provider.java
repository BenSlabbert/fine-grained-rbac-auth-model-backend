/* Licensed under Apache-2.0 2024. */
package org.example.app.di;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import github.benslabbert.vdw.codegen.txmanager.PlatformTransactionManager;
import github.benslabbert.vdw.codegen.txmanager.TransactionManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.app.config.ConfigModule;
import org.example.app.web.RouterFactory;
import org.example.app.web.ServerFactory;
import org.example.app.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Component(modules = {Provider.EagerModule.class, ConfigModule.class, WebModule.class})
public interface Provider {

  Logger log = LoggerFactory.getLogger(Provider.class);

  RouterFactory routerFactory();

  ServerFactory serverFactory();

  @Nullable Void init();

  default void closeSilently() {
    try {
      PlatformTransactionManager.close();
    } catch (Exception e) {
      log.error("Error closing transaction manager", e);
    }
  }

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
    @Nullable static Void provideEager(TransactionManager transactionManager) {
      log.info("eager init");
      PlatformTransactionManager.setTransactionManager(transactionManager);
      return null;
    }
  }
}
