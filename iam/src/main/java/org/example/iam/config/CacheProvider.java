/* Licensed under Apache-2.0 2026. */
package org.example.iam.config;

import static org.example.iam.di.Provider.PERMISSIONS_CACHE;

import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.aop.cache.Cache;
import github.benslabbert.vdw.codegen.aop.cache.CacheManager;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Optional;

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
        private static final com.google.common.cache.Cache<String, Object> GUAVA_CACHE =
            com.google.common.cache.CacheBuilder.newBuilder()
                .initialCapacity(64)
                .maximumSize(4096)
                .expireAfterWrite(Duration.ofSeconds(10L))
                .build();

        @Override
        public Object get(String key) {
          return GUAVA_CACHE.getIfPresent(key);
        }

        @Override
        public void put(String key, Object value) {
          GUAVA_CACHE.put(key, value);
        }

        @Override
        public void evict(String key) {
          GUAVA_CACHE.invalidate(key);
        }
      };
}
