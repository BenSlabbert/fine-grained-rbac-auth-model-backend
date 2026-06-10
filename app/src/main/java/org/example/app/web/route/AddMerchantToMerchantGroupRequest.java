/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@JsonWriter
@GenerateBuilder
public record AddMerchantToMerchantGroupRequest(
    @NotBlank String merchantGroupName, @NotNull Set<String> merchantNames) {}
