/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.PathParams;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Response;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcQueryRunner;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcQueryRunnerFactory;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtils;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtilsFactory;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import io.vertx.core.http.HttpServerResponse;
import jakarta.inject.Inject;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.app.entity.Application;
import org.example.app.entity.ApplicationBuilder;
import org.example.app.entity.ApplicationRepository;
import org.example.app.entity.Permission;
import org.example.app.entity.PermissionBuilder;
import org.example.app.entity.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/application")
class ApplicationHandler {

  private static final Logger log = LoggerFactory.getLogger(ApplicationHandler.class);

  private final ApplicationRepository applicationRepository;
  private final PermissionRepository permissionRepository;
  private final JdbcQueryRunner jdbcQueryRunner;
  private final JdbcUtils jdbcUtils;

  @Inject
  ApplicationHandler(
      JdbcQueryRunnerFactory jdbcQueryRunnerFactory,
      JdbcUtilsFactory jdbcUtilsFactory,
      ApplicationRepository applicationRepository,
      PermissionRepository permissionRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.jdbcQueryRunner = jdbcQueryRunnerFactory.create(cfg);
    this.applicationRepository = applicationRepository;
    this.permissionRepository = permissionRepository;
  }

  @Put(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateApplicationRequest request) {
    log.info("create application {}", request.name());
    Application a = ApplicationBuilder.builder().name(request.name()).build();
    applicationRepository.doInTransaction().save(a);
  }

  @Transactional
  @Post(path = "/permissions")
  @HasRole("admin")
  void addPermissions(@Response HttpServerResponse response, @Body AddPermissionsRequest request) {
    log.info(
        "add permissions {} to application {}", request.permissions(), request.applicationName());

    Optional<Application> maybeApplication = applicationRepository.name(request.applicationName());
    if (maybeApplication.isEmpty()) {
      response.setStatusCode(404).end();
      return;
    }

    Reference<Application> ref = Reference.of(maybeApplication.get().id());

    List<Permission> permissions =
        request.permissions().stream()
            .map(p -> PermissionBuilder.builder().application(ref).value(p).build())
            .toList();

    jdbcQueryRunner.update("delete from permission where application_id = ?", ref.id());
    permissionRepository.insertAll(permissions);
    response.setStatusCode(201).end();
  }

  @Get(path = "/permissions/{string:appName}")
  @HasRole("admin")
  GetPermissionsResponse getApplicationPermissions(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_GetApplicationPermissions_ParamParser.parse(pathParams);
    var appName = params.appName();
    log.info("get permissions for application {}", appName);

    try (var s =
        jdbcUtils.streamInTransaction(
            """
            select p.value from permission p
            join application a on a.id = p.application_id
            where a.name = ?
            order by p.id
            """,
            rs -> rs.getString(1),
            appName)) {
      return new GetPermissionsResponse(s.toList());
    }
  }

  @Get(path = "/permissions/{string:appName}/{string:userName}")
  @HasRole("admin")
  GetPermissionsResponse getApplicationUserPermissions(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_GetApplicationUserPermissions_ParamParser.parse(pathParams);
    var appName = params.appName();
    var userName = params.userName();
    log.info("get permissions for application {} and userId {}", appName, userName);

    try (var s =
        jdbcUtils.streamInTransaction(
            """
            select p.value from permission p
            join application a on a.id = p.application_id
            join "user" u on u.id = p.application_id
            join user_role ur on ur.user_id = u.id
            join "role" r on r.id = ur.role_id
            join role_permission rp on rp.role_id = r.id
            where a.name = ? and u.name = ? and rp.permission_id = p.id
            order by p.id
            """,
            rs -> rs.getString(1),
            appName,
            userName)) {
      return new GetPermissionsResponse(s.toList());
    }
  }

  @Get(path = "/permissions/{string:appName}/{string:userName}/{string:permission}")
  @HasRole("admin")
  HasPermissionResponse hasPermission(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_HasPermission_ParamParser.parse(pathParams);
    var appName = params.appName();
    var userName = params.userName();
    var permission = params.permission();
    log.info(
        "check permission for application {} and userId {} and permission {}",
        appName,
        userName,
        permission);

    boolean hasPermission =
        jdbcQueryRunner.query(
            """
            select p.value from permission p
            join application a on a.id = p.application_id
            join "user" u on u.id = p.application_id
            join user_role ur on ur.user_id = u.id
            join "role" r on r.id = ur.role_id
            join role_permission rp on rp.role_id = r.id
            where a.name = ? and u.name = ? and rp.permission_id = p.id and p.value = ?
            order by p.id
            """,
            ResultSet::next,
            appName,
            userName,
            permission);

    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }
}
