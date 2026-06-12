/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import java.util.List;

@JsonWriter
@GenerateBuilder
public record GetPermissionsResponse(List<String> permissions) {}
