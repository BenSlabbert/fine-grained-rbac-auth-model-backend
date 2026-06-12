/* Licensed under Apache-2.0 2026. */
package org.example.iam.service;

import github.benslabbert.vdw.codegen.annotation.transaction.Transactional;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcQueryRunner;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcQueryRunnerFactory;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtils;
import github.benslabbert.vdw.codegen.commons.jdbc.JdbcUtilsFactory;
import jakarta.inject.Inject;
import java.sql.ResultSet;
import java.util.List;
import org.apache.commons.dbutils.StatementConfiguration;
import org.example.iam.entity.MerchantRepository;
import org.example.iam.entity.PspRepository;
import org.example.iam.entity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final JdbcQueryRunner jdbcQueryRunner;
  private final JdbcUtils jdbcUtils;
  private final UserRepository userRepository;
  private final PspRepository pspRepository;
  private final MerchantRepository merchantRepository;

  @Inject
  AuthService(
      JdbcQueryRunnerFactory jdbcQueryRunnerFactory,
      JdbcUtilsFactory jdbcUtilsFactory,
      MerchantRepository merchantRepository,
      UserRepository userRepository,
      PspRepository pspRepository) {
    var cfg = new StatementConfiguration.Builder().fetchSize(10).build();
    this.jdbcQueryRunner = jdbcQueryRunnerFactory.create(cfg);
    this.jdbcUtils = jdbcUtilsFactory.create(cfg);
    this.merchantRepository = merchantRepository;
    this.userRepository = userRepository;
    this.pspRepository = pspRepository;
  }

  public List<String> getApplicationUserPermissions(String appName, String userName) {
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
      return s.toList();
    }
  }

  public boolean hasPermission(String appName, String userName, String permission) {
    return jdbcUtils.doInTransaction(
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
  }

  @Transactional
  public boolean userHasPspScope(String userName, String pspName) {
    long userId = userRepository.name(userName).orElseThrow().id();
    long pspId = pspRepository.name(pspName).orElseThrow().id();

    return jdbcQueryRunner.query(
        """
        select 1 from user_psp_scope
        where user_id = ? and psp_id = ?
        """,
        ResultSet::next,
        userId,
        pspId);
  }

  @Transactional
  public boolean userHasMerchantScope(String userName, String merchantName) {
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
      return true;
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
      return true;
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
      return true;
    }

    return jdbcQueryRunner.query(
        """
        select 1 from user_custom_merchant_group_scope s
        join custom_merchant_group cmg on s.custom_merchant_group_id = cmg.id
        join custom_merchant_group_merchant cmgm on cmgm.custom_merchant_group_id = cmg.id
        where s.user_id = ? and cmgm.merchant_id = ?
        """,
        ResultSet::next,
        userId,
        merchantId);
  }

  public boolean userHasMerchantGroupScope(String userName, String merchantGroupName) {
    return jdbcUtils.doInTransaction(
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
  }
}
