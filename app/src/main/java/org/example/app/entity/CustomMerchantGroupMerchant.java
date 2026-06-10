/* Licensed under Apache-2.0 2026. */
package org.example.app.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("custom_merchant_group_merchant")
@GenerateBuilder
public record CustomMerchantGroupMerchant(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("custom_merchant_group_id") Reference<CustomMerchantGroup> customMerchantGroup,
    @Column("merchant_id") Reference<Merchant> merchant)
    implements Reference<CustomMerchantGroupMerchant> {}
