/* Licensed under Apache-2.0 2024. */
package org.example.iam.di;

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
import org.example.iam.config.ConfigModule;
import org.example.iam.entity.EntityModule;
import org.example.iam.web.RouterFactory;
import org.example.iam.web.ServerFactory;
import org.example.iam.web.WebModule;
import org.example.utilities.FlywayUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Component(
    modules = {Provider.EagerModule.class, ConfigModule.class, WebModule.class, EntityModule.class})
public interface Provider {

  Logger log = LoggerFactory.getLogger(Provider.class);

  RouterFactory routerFactory();

  ServerFactory serverFactory();

  DataSource dataSource();

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
