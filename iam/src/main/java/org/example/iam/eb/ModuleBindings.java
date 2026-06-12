/* Licensed under Apache-2.0 2026. */
package org.example.iam.eb;

import dagger.Binds;
import dagger.Module;
import org.example.security.api.SecurityService;

@Module
interface ModuleBindings {

  @Binds
  SecurityService securityService(SecurityServiceImpl securityService);
}
