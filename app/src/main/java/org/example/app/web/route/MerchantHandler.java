/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.inject.Inject;
import org.example.app.entity.MerchantBuilder;
import org.example.app.entity.MerchantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/merchant")
class MerchantHandler {

  private static final Logger log = LoggerFactory.getLogger(MerchantHandler.class);

  private final MerchantRepository repository;

  @Inject
  MerchantHandler(MerchantRepository repository) {
    this.repository = repository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateMerchantRequest request) {
    log.info("create merchant {}", request.name());
    MerchantBuilder.Builder builder = MerchantBuilder.builder().name(request.name());

    if (null != request.pspId()) {
      builder.psp(Reference.of(request.pspId()));
    }

    repository.doInTransaction().save(builder.build());
  }
}
