/* Licensed under Apache-2.0 2026. */
package org.example.iam.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("merchant_psp")
@GenerateBuilder
public record MerchantPsp(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("merchant_id") Reference<Merchant> merchant,
    @Column("psp_id") Reference<Psp> psp)
    implements Reference<MerchantPsp> {}
