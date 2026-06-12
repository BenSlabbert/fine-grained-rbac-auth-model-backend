/* Licensed under Apache-2.0 2026. */
package org.example.iam.external;

import dagger.Module;
import org.example.security.api.SecurityApiModule;

@Module(includes = SecurityApiModule.class)
public interface ExternalModule {}
