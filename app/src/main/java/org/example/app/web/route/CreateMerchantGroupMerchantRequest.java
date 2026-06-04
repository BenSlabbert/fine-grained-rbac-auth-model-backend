/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.json.JsonWriter;

@JsonWriter
@GenerateBuilder
record CreateMerchantGroupMerchantRequest(long merchantGroupId, long merchantId) {}
