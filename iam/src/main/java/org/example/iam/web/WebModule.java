/* Licensed under Apache-2.0 2026. */
package org.example.iam.web;

import dagger.Module;
import org.example.iam.web.route.RouteModule;

@Module(includes = RouteModule.class)
public interface WebModule {}
