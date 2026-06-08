/* Licensed under Apache-2.0 2026. */
package org.example.app.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;
import jakarta.annotation.Nullable;

@Table("merchant")
@GenerateBuilder
public record Merchant(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("name") String name,
    @Nullable @Column("psp_id") Reference<Psp> psp,
    @Nullable @Column("merchant_group_id") Reference<MerchantGroup> merchantGroup)
    implements Reference<Merchant> {}
