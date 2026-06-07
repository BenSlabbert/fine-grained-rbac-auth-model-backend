/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@JsonWriter
@GenerateBuilder
public record CreateMerchantRequest(@NotBlank String name, @Nullable Long pspId) {}
