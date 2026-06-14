/* Licensed under Apache-2.0 2024. */
package org.example.gateway.config;

import dagger.Module;

@Module(
    includes = {
      // internal modules
      ConfigModuleBindings.class,
      SessionHandlerProvider.class,
      SessionStoreProvider.class,
    })
public interface ConfigModule {}
