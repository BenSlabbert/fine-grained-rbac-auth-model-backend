/* Licensed under Apache-2.0 2026. */
package org.example.app.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("user_custom_merchant_group_scope")
@GenerateBuilder
public record UserCustomMerchantGroupScope(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("user_id") Reference<User> user,
    @Column("custom_merchant_group_id") Reference<CustomMerchantGroup> customMerchantGroup)
    implements Reference<UserCustomMerchantGroupScope> {}
