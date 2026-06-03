/* Licensed under Apache-2.0 2024. */
package org.example.app.config;

import dagger.Binds;
import dagger.Module;
import github.benslabbert.vdw.codegen.commons.RoleAuthorizationHandlerProvider;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcTransactionManager;
import github.benslabbert.vdw.codegen.txmanager.TransactionManager;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;

@Module
interface ConfigModuleBindings {

  @Binds
  RoleAuthorizationHandlerProvider roleAuthorizationHandlerProvider(AuthorizationHandlerProvider p);

  @Binds
  AuthorizationProvider authorizationProvider(AuthorizationHandlerProvider p);

  @Binds
  AuthenticationProvider authenticationProvider(AuthenticationHandlerProvider p);

  @Binds
  TransactionManager transactionManager(JdbcTransactionManager p);
}
