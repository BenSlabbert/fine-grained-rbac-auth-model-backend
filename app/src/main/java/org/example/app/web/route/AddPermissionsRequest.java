/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@JsonWriter
@GenerateBuilder
record AddPermissionsRequest(
    long applicationId, @NotNull @NotEmpty @Size(min = 1, max = 2) Set<String> permissions) {}
