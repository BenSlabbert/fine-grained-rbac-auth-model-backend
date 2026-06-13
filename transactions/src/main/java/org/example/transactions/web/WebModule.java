/* Licensed under Apache-2.0 2026. */
package org.example.transactions.web;

import dagger.Module;
import org.example.transactions.web.route.RouteModule;

@Module(includes = RouteModule.class)
public interface WebModule {}
