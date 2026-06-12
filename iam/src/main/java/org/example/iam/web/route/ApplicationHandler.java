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
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.iam.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/application")
class ApplicationHandler {

  private static final Logger log = LoggerFactory.getLogger(ApplicationHandler.class);

  private final ApplicationRepository applicationRepository;
  private final PermissionRepository permissionRepository;
  private final MerchantRepository merchantRepository;
  private final JdbcQueryRunner jdbcQueryRunner;
  private final UserRepository userRepository;
  private final PspRepository pspRepository;
  private final JdbcUtils jdbcUtils;

  @Inject
  ApplicationHandler(
      JdbcQueryRunnerFactory jdbcQueryRunnerFactory,
      JdbcUtilsFactory jdbcUtilsFactory,
      UserRepository userRepository,
      PspRepository pspRepository,
      MerchantRepository merchantRepository,
      ApplicationRepository applicationRepository,
      PermissionRepository permissionRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcQueryRunner = jdbcQueryRunnerFactory.create(cfg);
    this.userRepository = userRepository;
    this.pspRepository = pspRepository;
    this.merchantRepository = merchantRepository;
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
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

    try (var s =
        jdbcUtils.streamInTransaction(
            """
            select p.value from permission p
            join application a on a.id = p.application_id
            join role_permission rp on rp.permission_id = p.id
            join "role" r on r.id = rp.role_id
            join user_role ur on ur.role_id = r.id
            join "user" u on u.id = ur.user_id
            where a.name = ? and u.name = ?
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

    Boolean hasPermission =
        jdbcUtils.doInTransaction(
            _ ->
                jdbcQueryRunner.query(
                    """
                    select p.value from permission p
                    join application a on a.id = p.application_id
                    join role_permission rp on rp.permission_id = p.id
                    join "role" r on r.id = rp.role_id
                    join user_role ur on ur.role_id = r.id
                    join "user" u on u.id = ur.user_id
                    where a.name = ? and u.name = ? and p.value = ?
                    order by p.id
                    """,
                    ResultSet::next,
                    appName,
                    userName,
                    permission));

    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }

  @Transactional
  @Get(path = "/scope/psp/{string:userName}/{string:pspName}")
  @HasRole("admin")
  HasPermissionResponse userHasPspScope(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_UserHasPspScope_ParamParser.parse(pathParams);
    var userName = params.userName();
    var pspName = params.pspName();

    long userId = userRepository.name(userName).orElseThrow().id();
    long pspId = pspRepository.name(pspName).orElseThrow().id();

    boolean hasPermission =
        jdbcQueryRunner.query(
            """
            select 1 from user_psp_scope
            where user_id = ? and psp_id = ?
            """,
            ResultSet::next,
            userId,
            pspId);

    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }

  @Transactional
  @Get(path = "/scope/merchant/{string:userName}/{string:merchantName}")
  @HasRole("admin")
  HasPermissionResponse userHasMerchantScope(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_UserHasMerchantScope_ParamParser.parse(pathParams);
    var userName = params.userName();
    var merchantName = params.merchantName();

    long userId = userRepository.name(userName).orElseThrow().id();
    long merchantId = merchantRepository.name(merchantName).orElseThrow().id();

    boolean hasPermission =
        jdbcQueryRunner.query(
            """
            select 1 from user_merchant_scope
            where user_id = ? and merchant_id = ?
            """,
            ResultSet::next,
            userId,
            merchantId);

    if (hasPermission) {
      return HasPermissionResponseBuilder.builder().hasPermission(true).build();
    }

    hasPermission =
        jdbcQueryRunner.query(
            """
            select 1 from user_merchant_group_scope s
            join merchant_group mg on s.merchant_group_id = mg.id
            join merchant_merchant_group mmg on mmg.merchant_group_id = mg.id
            where s.user_id = ? and mmg.merchant_id = ?
            """,
            ResultSet::next,
            userId,
            merchantId);

    if (hasPermission) {
      return HasPermissionResponseBuilder.builder().hasPermission(true).build();
    }

    hasPermission =
        jdbcQueryRunner.query(
            """
            select 1 from user_psp_scope s
            join merchant_psp mp on s.psp_id = mp.psp_id
            where s.user_id = ? and mp.merchant_id = ?
            """,
            ResultSet::next,
            userId,
            merchantId);

    if (hasPermission) {
      return HasPermissionResponseBuilder.builder().hasPermission(true).build();
    }

    hasPermission =
        jdbcQueryRunner.query(
            """
            select 1 from user_custom_merchant_group_scope s
            join custom_merchant_group cmg on s.custom_merchant_group_id = cmg.id
            join custom_merchant_group_merchant cmgm on cmgm.custom_merchant_group_id = cmg.id
            where s.user_id = ? and cmgm.merchant_id = ?
            """,
            ResultSet::next,
            userId,
            merchantId);

    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }

  @Get(path = "/scope/merchant-group/{string:userName}/{string:merchantGroupName}")
  @HasRole("admin")
  HasPermissionResponse userHasMerchantGroupScope(@PathParams Map<String, String> pathParams) {
    var params = ApplicationHandler_UserHasMerchantGroupScope_ParamParser.parse(pathParams);
    var userName = params.userName();
    var merchantGroupName = params.merchantGroupName();

    boolean hasPermission =
        jdbcUtils.doInTransaction(
            _ ->
                jdbcQueryRunner.query(
                    """
                    select 1 from user_merchant_group_scope umgs
                    join "user" u on u.id = umgs.user_id
                    join merchant_group mg on mg.id = umgs.merchant_group_id
                    where u.name = ? and mg.name = ?
                    """,
                    ResultSet::next,
                    userName,
                    merchantGroupName));

    return HasPermissionResponseBuilder.builder().hasPermission(hasPermission).build();
  }
}
