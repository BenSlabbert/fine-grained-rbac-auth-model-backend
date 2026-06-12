/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

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
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.iam.entity.*;
import org.example.iam.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/application")
class ApplicationHandler {

  private static final Logger log = LoggerFactory.getLogger(ApplicationHandler.class);

  private final ApplicationRepository applicationRepository;
  private final PermissionRepository permissionRepository;
  private final JdbcQueryRunner jdbcQueryRunner;
  private final JdbcUtils jdbcUtils;
  private final AuthService authService;

  @Inject
  ApplicationHandler(
      AuthService authService,
      JdbcQueryRunnerFactory jdbcQueryRunnerFactory,
      JdbcUtilsFactory jdbcUtilsFactory,
      ApplicationRepository applicationRepository,
      PermissionRepository permissionRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcQueryRunner = jdbcQueryRunnerFactory.create(cfg);
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.applicationRepository = applicationRepository;
    this.permissionRepository = permissionRepository;
    this.authService = authService;
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

    Reference<Application> ref =
        applicationRepository.name(request.applicationName()).orElseThrow();

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
    var permissions = authService.getApplicationUserPermissions(appName, userName);
    return new GetPermissionsResponse(permissions);
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

    var hasPermission = authService.hasPermission(appName, userName, permission);
    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }

  @Get(path = "/scope/psp/{string:userName}/{string:pspName}")
  @HasRole("admin")
  HasPermissionResponse userHasPspScope(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_UserHasPspScope_ParamParser.parse(pathParams);
    var userName = params.userName();
    var pspName = params.pspName();
    boolean hasPermission = authService.userHasPspScope(userName, pspName);
    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }

  @Get(path = "/scope/merchant/{string:userName}/{string:merchantName}")
  @HasRole("admin")
  HasPermissionResponse userHasMerchantScope(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_UserHasMerchantScope_ParamParser.parse(pathParams);
    var userName = params.userName();
    var merchantName = params.merchantName();
    boolean hasPermission = authService.userHasMerchantScope(userName, merchantName);
    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }

  @Get(path = "/scope/merchant-group/{string:userName}/{string:merchantGroupName}")
  @HasRole("admin")
  HasPermissionResponse userHasMerchantGroupScope(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_UserHasMerchantGroupScope_ParamParser.parse(pathParams);
    var userName = params.userName();
    var merchantGroupName = params.merchantGroupName();
    boolean hasPermission = authService.userHasMerchantGroupScope(userName, merchantGroupName);
    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }
}
