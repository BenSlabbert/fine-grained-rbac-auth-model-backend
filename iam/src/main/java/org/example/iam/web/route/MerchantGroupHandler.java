/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import static java.util.Collections.nCopies;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtils;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtilsFactory;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.iam.entity.Merchant;
import org.example.iam.entity.MerchantGroup;
import org.example.iam.entity.MerchantGroupBuilder;
import org.example.iam.entity.MerchantGroupRepository;
import org.example.iam.entity.MerchantMerchantGroup;
import org.example.iam.entity.MerchantMerchantGroupBuilder;
import org.example.iam.entity.MerchantMerchantGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/merchant-group")
class MerchantGroupHandler {

  private static final Logger log = LoggerFactory.getLogger(MerchantGroupHandler.class);

  private final MerchantMerchantGroupRepository merchantMerchantGroupRepository;
  private final MerchantGroupRepository merchantGroupRepository;
  private final JdbcUtils jdbcUtils;

  @Inject
  MerchantGroupHandler(
      JdbcUtilsFactory jdbcUtilsFactory,
      MerchantMerchantGroupRepository merchantMerchantGroupRepository,
      MerchantGroupRepository merchantGroupRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.merchantMerchantGroupRepository = merchantMerchantGroupRepository;
    this.merchantGroupRepository = merchantGroupRepository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateMerchantGroupRequest request) {
    log.info("create merchant-group {}", request.name());

    MerchantGroup mg = MerchantGroupBuilder.builder().name(request.name()).build();

    merchantGroupRepository.doInTransaction().save(mg);
  }

  @Transactional
  @Post(path = "/add")
  @HasRole("admin")
  void assignMerchants(@Body AddMerchantToMerchantGroupRequest request) {
    log.info(
        "add merchant {} to  merchant-group {}",
        request.merchantNames(),
        request.merchantGroupName());

    MerchantGroup merchantGroup =
        merchantGroupRepository.name(request.merchantGroupName()).orElseThrow();

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

    List<MerchantMerchantGroup> list =
        request.merchantNames().stream()
            .map(
                r ->
                    MerchantMerchantGroupBuilder.builder()
                        .merchant(map.get(r))
                        .merchantGroup(merchantGroup)
                        .build())
            .toList();

    merchantMerchantGroupRepository.insertAll(list);
  }
}
