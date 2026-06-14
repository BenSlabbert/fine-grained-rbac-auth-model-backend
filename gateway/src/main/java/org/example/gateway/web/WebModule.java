/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web;

import dagger.Module;
import org.example.gateway.web.route.RouteModule;

@Module(includes = RouteModule.class)
public interface WebModule {}
