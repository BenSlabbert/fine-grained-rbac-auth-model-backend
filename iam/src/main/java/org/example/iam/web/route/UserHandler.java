/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Body;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import jakarta.inject.Inject;
import org.example.iam.entity.UserBuilder;
import org.example.iam.entity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/user")
class UserHandler {

  private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

  private final UserRepository repository;

  @Inject
  UserHandler(UserRepository repository) {
    this.repository = repository;
  }

  @Post(responseCode = 201)
  @HasRole("admin")
  void create(@Body CreateUserRequest request) {
    log.info("create user {}", request.name());
    var u = UserBuilder.builder().name(request.name()).build();
    repository.doInTransaction().save(u);
  }
}
