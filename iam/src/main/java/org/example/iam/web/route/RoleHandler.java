/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import static java.util.Collections.*;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.commons.jdbc.*;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.iam.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/role")
class RoleHandler {

  private static final Logger log = LoggerFactory.getLogger(RoleHandler.class);

  private final RolePermissionRepository rolePermissionRepository;
  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final JdbcUtils jdbcUtils;

  @Inject
  RoleHandler(
      JdbcUtilsFactory jdbcUtilsFactory,
      RolePermissionRepository rolePermissionRepository,
      UserRoleRepository userRoleRepository,
      RoleRepository roleRepository,
      UserRepository userRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.rolePermissionRepository = rolePermissionRepository;
    this.userRoleRepository = userRoleRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }

  @HasRole("admin")
  @Post(responseCode = 201)
  void create(@Body CreateRoleRequest request) {
    log.info("create role {}", request.name());
    roleRepository.doInTransaction().save(RoleBuilder.builder().name(request.name()).build());
  }

  @Transactional
  @HasRole("admin")
  @Post(path = "/assign", responseCode = 201)
  void assignRolePermissions(@Body AssignRolePermissionsRequest request) {
    log.info("assign permissions {} to  role {}", request.permissionValues(), request.roleName());

    Role role = roleRepository.name(request.roleName()).orElseThrow();

    String placeholders = String.join(", ", nCopies(request.permissionValues().size(), "?"));

    List<RolePermission> rolePermissions =
        jdbcUtils.stream(
                """
                select id from permission where value in (%s)
                """
                    .formatted(placeholders),
                r -> r.getLong(1),
                request.permissionValues().toArray())
            .map(
                id ->
                    RolePermissionBuilder.builder().role(role).permission(Reference.of(id)).build())
            .toList();

    rolePermissionRepository.insertAll(rolePermissions);
  }

  @Transactional
  @HasRole("admin")
  @Post(path = "/assign/user", responseCode = 201)
  void assignUserRole(@Body AssignUserRoleRequest request) {
    log.info("assign user {} to roles {}", request.userName(), request.roleNames());

    User user = userRepository.name(request.userName()).orElseThrow();

    String placeholders = String.join(", ", nCopies(request.roleNames().size(), "?"));

    List<UserRole> userRoles =
        jdbcUtils.stream(
                """
                select id from role where name in (%s)
                """
                    .formatted(placeholders),
                r -> r.getLong(1),
                request.roleNames().toArray())
            .map(id -> UserRoleBuilder.builder().role(Reference.of(id)).user(user).build())
            .toList();

    userRoleRepository.insertAll(userRoles);
  }
}
