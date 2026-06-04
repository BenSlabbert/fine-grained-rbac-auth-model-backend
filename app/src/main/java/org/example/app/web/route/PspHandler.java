/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import jakarta.inject.Inject;
import org.example.app.entity.Psp;
import org.example.app.entity.PspBuilder;
import org.example.app.entity.PspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/psp")
class PspHandler {

  private static final Logger log = LoggerFactory.getLogger(PspHandler.class);

  private final PspRepository repository;

  @Inject
  PspHandler(PspRepository repository) {
    this.repository = repository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreatePspRequest request) {
    log.info("create PSP {}", request.name());
    Psp psp = PspBuilder.builder().name(request.name()).build();
    repository.doInTransaction().save(psp);
  }
}
