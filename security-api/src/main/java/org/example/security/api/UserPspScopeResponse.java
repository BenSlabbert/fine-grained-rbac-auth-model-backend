/* Licensed under Apache-2.0 2026. */
package org.example.security.api;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;

@JsonWriter
@GenerateBuilder
public record UserPspScopeResponse(boolean hasPermission) {}
