/* Licensed under Apache-2.0 2026. */
package org.example.security.api;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.NotBlank;

@JsonWriter
@GenerateBuilder
public record UserMerchantGroupScopeRequest(
    @NotBlank String user, @NotBlank String merchantGroup) {}
