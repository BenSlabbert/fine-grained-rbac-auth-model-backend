/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import static java.util.Collections.nCopies;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import github.benslabbert.vdw.codegen.commons.jdbc.*;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.app.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/merchant")
class MerchantHandler {

  private static final Logger log = LoggerFactory.getLogger(MerchantHandler.class);

  private final CustomMerchantGroupMerchantRepository customMerchantGroupMerchantRepository;
  private final CustomMerchantGroupRepository customMerchantGroupRepository;
  private final MerchantPspRepository merchantPspRepository;
  private final PspRepository pspRepository;
  private final MerchantRepository merchantRepository;
  private final JdbcUtils jdbcUtils;

  @Inject
  MerchantHandler(
      JdbcUtilsFactory jdbcUtilsFactory,
      MerchantPspRepository merchantPspRepository,
      PspRepository pspRepository,
      CustomMerchantGroupMerchantRepository customMerchantGroupMerchantRepository,
      CustomMerchantGroupRepository customMerchantGroupRepository,
      MerchantRepository merchantRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.customMerchantGroupMerchantRepository = customMerchantGroupMerchantRepository;
    this.customMerchantGroupRepository = customMerchantGroupRepository;
    this.merchantPspRepository = merchantPspRepository;
    this.pspRepository = pspRepository;
    this.merchantRepository = merchantRepository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateMerchantRequest request) {
    log.info("create merchant {}", request.name());
    MerchantBuilder.Builder builder = MerchantBuilder.builder().name(request.name());
    merchantRepository.doInTransaction().save(builder.build());
  }

  @Put(responseCode = 201, path = "/custom-merchant-group")
  @HasRole("admin")
  void createCustomMerchantGroup(@Body CreateCustomMerchantGroup request) {
    log.info("create custom merchant group {}", request.name());
    CustomMerchantGroupBuilder.Builder builder =
        CustomMerchantGroupBuilder.builder().name(request.name());

    customMerchantGroupRepository.doInTransaction().save(builder.build());
  }

  @Transactional
  @Post(path = "/custom-merchant-group/add")
  @HasRole("admin")
  void addMerchantToCustomMerchantGroup(@Body AddMerchantToCustomMerchantGroupRequest request) {
    log.info(
        "add merchant {} to custom merchant group {}",
        request.merchantNames(),
        request.customMerchantGroupName());

    CustomMerchantGroup customMerchantGroup =
        customMerchantGroupRepository.name(request.customMerchantGroupName()).orElseThrow();

    String placeholders = String.join(", ", nCopies(request.merchantNames().size(), "?"));

    record RS(long id, String name) {}

    Map<String, Reference<Merchant>> map =
        jdbcUtils.stream(
                """
                select id, name from merchant where name in (%s)
                """
                    .formatted(placeholders),
                r -> new RS(r.getLong(1), r.getString(2)),
                request.merchantNames().toArray())
            .collect(Collectors.toMap(s -> s.name, s -> Reference.of(s.id)));

    List<CustomMerchantGroupMerchant> list =
        request.merchantNames().stream()
            .map(
                r ->
                    CustomMerchantGroupMerchantBuilder.builder()
                        .customMerchantGroup(customMerchantGroup)
                        .merchant(map.get(r))
                        .build())
            .toList();

    customMerchantGroupMerchantRepository.insertAll(list);
  }

  @Transactional
  @Post(path = "/psp")
  @HasRole("admin")
  void addMerchantToPsp(@Body AddMerchantToPspRequest request) {
    log.info("add merchant {} to psp {}", request.merchantName(), request.pspName());

    var merchant = merchantRepository.name(request.merchantName()).orElseThrow();
    var psp = pspRepository.name(request.pspName()).orElseThrow();

    merchantPspRepository.save(MerchantPspBuilder.builder().merchant(merchant).psp(psp).build());
  }
}
