/* Licensed under Apache-2.0 2026. */
package org.example.iam.entity;

import github.benslabbert.vdw.codegen.annotation.builder.GenerateBuilder;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Column;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.FindOneByColumn;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Id;
import github.benslabbert.vdw.codegen.annotation.jdbc.Table.Version;
import github.benslabbert.vdw.codegen.commons.jdbc.Reference;

@Table("psp")
@GenerateBuilder
public record Psp(
    @Column("id") @Id("id_sequence") long id,
    @Column("version") @Version int version,
    @Column("name") @FindOneByColumn String name)
    implements Reference<Psp> {}
