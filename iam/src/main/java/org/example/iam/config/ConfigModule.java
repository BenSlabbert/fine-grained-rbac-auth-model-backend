/* Licensed under Apache-2.0 2024. */
package org.example.iam.config;

import dagger.Module;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcTransactionManagerModule;

@Module(
    includes = {
      // internal modules
      ConfigModuleBindings.class,
      DataSourceProvider.class,
      CacheProvider.class,
      JWTAuthProvider.class,
      EBAuthenticationProvider.class,
      // external modules
      JdbcTransactionManagerModule.class
    })
public interface ConfigModule {}
