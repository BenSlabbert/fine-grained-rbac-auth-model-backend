/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Put;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.inject.Inject;
import java.util.List;
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

  @Inject
  ApplicationHandler(
      ApplicationRepository applicationRepository, PermissionRepository permissionRepository) {
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

  @Post(responseCode = 201, path = "/permission")
  @HasRole("admin")
  void addPermissions(@Body AddPermissionsRequest request) {
    log.info(
        "add permissions {} to application {}", request.permissions(), request.applicationId());

    List<Permission> permissions =
        request.permissions().stream()
            .map(
                p ->
                    PermissionBuilder.builder()
                        .application(Reference.of(request.applicationId()))
                        .value(p)
                        .build())
            .toList();

    permissionRepository.doInTransaction().insertAll(permissions);
  }
}
