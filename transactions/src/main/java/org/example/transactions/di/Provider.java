/* Licensed under Apache-2.0 2024. */
package org.example.transactions.di;

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
import javax.sql.DataSource;
import org.example.security.api.SecurityService;
import org.example.transactions.config.ConfigModule;
import org.example.transactions.external.ExternalModule;
import org.example.transactions.web.RouterFactory;
import org.example.transactions.web.ServerFactory;
import org.example.transactions.web.WebModule;
import org.example.utilities.FlywayUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Component(
    modules = {
      Provider.EagerModule.class,
      ConfigModule.class,
      WebModule.class,
      ExternalModule.class
    })
public interface Provider {

  Logger log = LoggerFactory.getLogger(Provider.class);

  RouterFactory routerFactory();

  ServerFactory serverFactory();

  @Nullable Void init();

  default void closeSilently() {
    try {
      log.info("Closing transaction manager");
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

    @BindsInstance
    Builder securityService(SecurityService securityService);

    Provider build();
  }

  @Module
  final class EagerModule {

    @Inject
    EagerModule() {}

    @Provides
    @Nullable static Void provideEager(TransactionManager transactionManager, DataSource dataSource) {
      log.info("eager init");
      PlatformTransactionManager.setTransactionManager(transactionManager);
      FlywayUtility.migrate(dataSource);
      return null;
    }
  }
}
