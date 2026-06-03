/* Licensed under Apache-2.0 2024. */
package org.example.app.config;

import dagger.Module;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcTransactionManagerModule;

@Module(
    includes = {
      // internal modules
      ConfigModuleBindings.class,
      SessionHandlerProvider.class,
      SessionStoreProvider.class,
      DataSourceProvider.class,
      // external modules
      JdbcTransactionManagerModule.class
    })
public interface ConfigModule {}
