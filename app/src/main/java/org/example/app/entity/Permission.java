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
public record Permission(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("application_id") Reference<Application> application,
    @Column("value") String value)
    implements Reference<Permission> {}
