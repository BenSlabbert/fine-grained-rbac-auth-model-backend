/* Licensed under Apache-2.0 2026. */
package org.example.transactions.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/ping")
class Ping {

  private static final Logger log = LoggerFactory.getLogger(Ping.class);

  @Inject
  Ping() {}

  @Get
  @HasRole("admin")
  String ping() {
    log.debug("Ping - pong");
    return "pong";
  }
}
