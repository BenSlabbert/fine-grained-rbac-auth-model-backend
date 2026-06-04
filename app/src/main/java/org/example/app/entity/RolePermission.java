/* Licensed under Apache-2.0 2026. */
package org.example.app.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("role_permission")
@GenerateBuilder
public record RolePermission(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("role_id") Reference<Role> role,
    @Column("permission_id") Reference<Permission> permission)
    implements Reference<RolePermission> {}
