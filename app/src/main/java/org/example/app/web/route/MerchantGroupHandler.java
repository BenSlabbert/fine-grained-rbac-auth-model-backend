/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import jakarta.inject.Inject;
import org.example.app.entity.MerchantGroup;
import org.example.app.entity.MerchantGroupBuilder;
import org.example.app.entity.MerchantGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/merchant-group")
class MerchantGroupHandler {

  private static final Logger log = LoggerFactory.getLogger(MerchantGroupHandler.class);

  private final MerchantGroupRepository merchantGroupRepository;

  @Inject
  MerchantGroupHandler(MerchantGroupRepository merchantGroupRepository) {
    this.merchantGroupRepository = merchantGroupRepository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateMerchantGroupRequest request) {
    log.info("create merchant-group {}", request.name());

    MerchantGroup mg = MerchantGroupBuilder.builder().name(request.name()).build();

    merchantGroupRepository.doInTransaction().save(mg);
  }
}
