/* Licensed under Apache-2.0 2026. */
package org.example.transactions.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.security.api.*;

@Module
class SecurityServiceProvider {

  @Singleton
  @Provides
  static SecurityService securityService(Vertx vertx, @Named("machine-jwt") JWTAuth jwtAuth) {
    return new SecurityService() {

      private SecurityServiceVertxEBClientProxy getProxy() {
        var jwtOptions = new JWTOptions().setSubject("transactions");
        var token = jwtAuth.generateToken(new JsonObject(), jwtOptions);
        var options = new DeliveryOptions().addHeader("auth-token", token);
        return new SecurityServiceVertxEBClientProxy(vertx, options);
      }

      @Override
      public Future<HasPermissionResponse> hasPermission(HasPermissionRequest request) {
        SecurityServiceVertxEBClientProxy proxy = getProxy();
        return proxy.hasPermission(request);
      }

      @Override
      public Future<ApplicationUserPermissionsResponse> getApplicationUserPermissions(
          ApplicationUserPermissionsRequest request) {
        SecurityServiceVertxEBClientProxy proxy = getProxy();
        return proxy.getApplicationUserPermissions(request);
      }

      @Override
      public Future<UserPspScopeResponse> userHasPspScope(UserPspScopeRequest request) {
        SecurityServiceVertxEBClientProxy proxy = getProxy();
        return proxy.userHasPspScope(request);
      }

      @Override
      public Future<UserMerchantScopeResponse> userHasMerchantScope(
          UserMerchantScopeRequest request) {
        SecurityServiceVertxEBClientProxy proxy = getProxy();
        return proxy.userHasMerchantScope(request);
      }

      @Override
      public Future<UserMerchantGroupScopeResponse> userHasMerchantGroupScope(
          UserMerchantGroupScopeRequest request) {
        SecurityServiceVertxEBClientProxy proxy = getProxy();
        return proxy.userHasMerchantGroupScope(request);
      }
    };
  }
}
