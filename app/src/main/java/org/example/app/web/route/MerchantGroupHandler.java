/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.inject.Inject;
import org.example.app.entity.MerchantGroup;
import org.example.app.entity.MerchantGroupBuilder;
import org.example.app.entity.MerchantGroupMerchant;
import org.example.app.entity.MerchantGroupMerchantBuilder;
import org.example.app.entity.MerchantGroupMerchantRepository;
import org.example.app.entity.MerchantGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/merchant-group")
class MerchantGroupHandler {

  private static final Logger log = LoggerFactory.getLogger(MerchantGroupHandler.class);

  private final MerchantGroupMerchantRepository merchantGroupMerchantRepository;
  private final MerchantGroupRepository merchantGroupRepository;

  @Inject
  MerchantGroupHandler(
      MerchantGroupRepository merchantGroupRepository,
      MerchantGroupMerchantRepository merchantGroupMerchantRepository) {
    this.merchantGroupRepository = merchantGroupRepository;
    this.merchantGroupMerchantRepository = merchantGroupMerchantRepository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateMerchantGroupRequest request) {
    log.info("create merchant-group {}", request.name());

    MerchantGroup mg = MerchantGroupBuilder.builder().name(request.name()).build();

    merchantGroupRepository.doInTransaction().save(mg);
  }

  @Put(responseCode = 201, path = "/merchant")
  @HasRole("admin")
  void addMerchant(@Body CreateMerchantGroupMerchantRequest request) {
    log.info(
        "add merchant {} to  merchant-group {}", request.merchantId(), request.merchantGroupId());

    MerchantGroupMerchant mgm =
        MerchantGroupMerchantBuilder.builder()
            .merchant(Reference.of(request.merchantId()))
            .merchantGroup(Reference.of(request.merchantGroupId()))
            .build();

    merchantGroupMerchantRepository.doInTransaction().save(mgm);
  }
}
