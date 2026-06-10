/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import static java.util.Collections.nCopies;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtils;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtilsFactory;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.app.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/scope/user")
class UserScopeHandler {

  private static final Logger log = LoggerFactory.getLogger(UserScopeHandler.class);

  private final UserCustomMerchantGroupScopeRepository userCustomMerchantGroupScopeRepository;
  private final UserMerchantGroupScopeRepository userMerchantGroupScopeRepository;
  private final UserMerchantScopeRepository userMerchantScopeRepository;
  private final UserPspScopeRepository userPspScopeRepository;
  private final UserRepository userRepository;
  private final JdbcUtils jdbcUtils;

  @Inject
  UserScopeHandler(
      JdbcUtilsFactory jdbcUtilsFactory,
      UserCustomMerchantGroupScopeRepository userCustomMerchantGroupScopeRepository,
      UserMerchantGroupScopeRepository userMerchantGroupScopeRepository,
      UserMerchantScopeRepository userMerchantScopeRepository,
      UserPspScopeRepository userPspScopeRepository,
      UserRepository userRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.userCustomMerchantGroupScopeRepository = userCustomMerchantGroupScopeRepository;
    this.userMerchantGroupScopeRepository = userMerchantGroupScopeRepository;
    this.userMerchantScopeRepository = userMerchantScopeRepository;
    this.userPspScopeRepository = userPspScopeRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  @Post(path = "/psp")
  @HasRole("admin")
  void addUserToPspScope(@Body AddUserToPspScopeRequest request) {
    log.info("add user {} to psps {}", request.userName(), request.pspNames());

    User user = userRepository.name(request.userName()).orElseThrow();

    String placeholders = String.join(", ", nCopies(request.pspNames().size(), "?"));

    record RS(long id, String name) {}

    Map<String, Reference<Psp>> map =
        jdbcUtils.stream(
                """
                select id, name from psp where name in (%s)
                """
                    .formatted(placeholders),
                r -> new RS(r.getLong(1), r.getString(2)),
                request.pspNames().toArray())
            .collect(Collectors.toMap(s -> s.name, s -> Reference.of(s.id)));

    List<UserPspScope> list =
        request.pspNames().stream()
            .map(r -> UserPspScopeBuilder.builder().user(user).psp(map.get(r)).build())
            .toList();

    userPspScopeRepository.insertAll(list);
  }

  @Transactional
  @Post(path = "/merchant")
  @HasRole("admin")
  void addUserToMerchantScope(@Body AddUserToMerchantScopeRequest request) {
    log.info("add user {} to merchant {}", request.userName(), request.merchantNames());

    User user = userRepository.name(request.userName()).orElseThrow();

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

    List<UserMerchantScope> list =
        request.merchantNames().stream()
            .map(r -> UserMerchantScopeBuilder.builder().user(user).merchant(map.get(r)).build())
            .toList();

    userMerchantScopeRepository.insertAll(list);
  }

  @Transactional
  @Post(path = "/merchant-group")
  @HasRole("admin")
  void addUserToMerchantGroupScope(@Body AddUserToMerchantGroupScopeRequest request) {
    log.info("add user {} to merchant group {}", request.userName(), request.merchantGroupNames());

    User user = userRepository.name(request.userName()).orElseThrow();

    String placeholders = String.join(", ", nCopies(request.merchantGroupNames().size(), "?"));

    record RS(long id, String name) {}

    Map<String, Reference<MerchantGroup>> map =
        jdbcUtils.stream(
                """
                select id, name from merchant_group where name in (%s)
                """
                    .formatted(placeholders),
                r -> new RS(r.getLong(1), r.getString(2)),
                request.merchantGroupNames().toArray())
            .collect(Collectors.toMap(s -> s.name, s -> Reference.of(s.id)));

    List<UserMerchantGroupScope> list =
        request.merchantGroupNames().stream()
            .map(
                r ->
                    UserMerchantGroupScopeBuilder.builder()
                        .user(user)
                        .merchantGroup(map.get(r))
                        .build())
            .toList();

    userMerchantGroupScopeRepository.insertAll(list);
  }

  @Transactional
  @Post(path = "/custom-merchant-group")
  @HasRole("admin")
  void addUserToCustomMerchantGroupScope(@Body AddUserToCustomMerchantGroupScopeRequest request) {
    log.info(
        "add user {} to custom merchant group {}",
        request.userName(),
        request.customMerchantGroupNames());

    User user = userRepository.name(request.userName()).orElseThrow();

    String placeholders =
        String.join(", ", nCopies(request.customMerchantGroupNames().size(), "?"));

    record RS(long id, String name) {}

    Map<String, Reference<CustomMerchantGroup>> map =
        jdbcUtils.stream(
                """
                select id, name from custom_merchant_group where name in (%s)
                """
                    .formatted(placeholders),
                r -> new RS(r.getLong(1), r.getString(2)),
                request.customMerchantGroupNames().toArray())
            .collect(Collectors.toMap(s -> s.name, s -> Reference.of(s.id)));

    List<UserCustomMerchantGroupScope> list =
        request.customMerchantGroupNames().stream()
            .map(
                r ->
                    UserCustomMerchantGroupScopeBuilder.builder()
                        .user(user)
                        .customMerchantGroup(map.get(r))
                        .build())
            .toList();

    userCustomMerchantGroupScopeRepository.insertAll(list);
  }
}
