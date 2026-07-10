/* Licensed under Apache-2.0 2026. */
package org.example.iam.config;

import static org.example.iam.di.Provider.PERMISSIONS_CACHE;

import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.aop.cache.Cache;
import github.benslabbert.vdw.codegen.aop.cache.CacheManager;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Module
final class CacheProvider {

  private CacheProvider() {}

  @Singleton
  @Provides
  static CacheManager cacheManager() {
    return cacheName -> {
      if (PERMISSIONS_CACHE.equals(cacheName)) {
        return Optional.of(IN_MEMORY_CACHE);
      }
      return Optional.empty();
    };
  }

  private static final Cache IN_MEMORY_CACHE =
      new Cache() {
        private static final Map<String, Object> m = new ConcurrentHashMap<>();

        @Override
        public Object get(String key) {
          return m.get(key);
        }

        @Override
        public void put(String key, Object value) {
          m.put(key, value);
        }

        @Override
        public void evict(String key) {
          m.remove(key);
        }
      };
}
