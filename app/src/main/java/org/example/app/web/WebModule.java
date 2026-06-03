/* Licensed under Apache-2.0 2026. */
package org.example.app.web;

import dagger.Module;
import org.example.app.web.route.RouteModule;

@Module(includes = RouteModule.class)
public interface WebModule {}
