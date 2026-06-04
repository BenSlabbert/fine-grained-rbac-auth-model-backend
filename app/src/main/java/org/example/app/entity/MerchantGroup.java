/* Licensed under Apache-2.0 2026. */
package org.example.app.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table
@GenerateBuilder
public record MerchantGroup(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("merchant_group_id") Reference<MerchantGroup> merchantGroup,
    @Column("merchant_id") Reference<Merchant> merchant)
    implements Reference<MerchantGroup> {}
