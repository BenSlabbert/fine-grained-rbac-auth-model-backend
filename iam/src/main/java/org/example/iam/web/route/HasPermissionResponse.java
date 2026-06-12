/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;

@JsonWriter
@GenerateBuilder
public record HasPermissionResponse(boolean hasPermission) {}
