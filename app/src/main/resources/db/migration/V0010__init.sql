create
  sequence id_sequence
start with
  1 increment by 1 maxvalue 9223372036854775807 CACHE 1;

create
  table
    psp(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index psp_name_idx on
  psp(name) include(id);

create
  table
    merchant_group(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index merchant_group_name_idx on
  merchant_group(name) include(id);

create
  table
    merchant(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index merchant_name_idx on
  merchant(name) include(id);

create
  table
    merchant_psp(
      id bigint not null primary key,
      version int4 not null,
      merchant_id bigint not null,
      psp_id bigint not null,
      constraint fk_merchant_psp_merchant_id foreign key(merchant_id) references merchant(id),
      constraint fk_merchant_psp_psp_id foreign key(psp_id) references psp,
      constraint uq_merchant_psp unique(
        merchant_id,
        psp_id
      )
    );

create
  table
    merchant_merchant_group(
      id bigint not null primary key,
      version int4 not null,
      merchant_id bigint not null,
      merchant_group_id bigint not null,
      constraint fk_merchant_merchant_group_merchant_id foreign key(merchant_id) references merchant(id),
      constraint fk_merchant_merchant_group_merchant_group_id foreign key(merchant_group_id) references merchant_group,
      constraint uq_merchant_merchant_group unique(
        merchant_id,
        merchant_group_id
      )
    );

create
  table
    application(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index application_name_idx on
  application(name) include(id);

create
  table
    permission(
      id bigint not null primary key,
      version int4 not null,
      application_id bigint not null,
      value varchar(255) not null unique,
      constraint fk_permission_application foreign key(application_id) references application(id)
    );

create
  table
    "user"(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index user_name_idx on
  "user"(name) include(id);

create
  table
    "role"(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index role_name_idx on
  "role"(name) include(id);

create
  table
    role_permission(
      id bigint not null primary key,
      version int4 not null,
      role_id bigint not null,
      permission_id bigint not null,
      constraint fk_role_permission_role foreign key(role_id) references "role"(id),
      constraint fk_role_permission_permission foreign key(permission_id) references permission(id),
      constraint uq_role_permission unique(
        role_id,
        permission_id
      )
    );

create
  table
    user_role(
      id bigint not null primary key,
      version int4 not null,
      user_id bigint not null,
      role_id bigint not null,
      constraint fk_user_role_user foreign key(user_id) references "user"(id),
      constraint fk_user_role_role foreign key(role_id) references "role"(id),
      constraint uq_user_role unique(
        user_id,
        role_id
      )
    );

create
  table
    custom_merchant_group(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null
    );

create
  unique index custom_merchant_group_name_idx on
  custom_merchant_group(name) include(id);

create
  table
    custom_merchant_group_merchant(
      id bigint not null primary key,
      version int4 not null,
      custom_merchant_group_id bigint not null,
      merchant_id bigint not null,
      constraint fk_custom_merchant_group_merchant_custom_merchant_group_id foreign key(custom_merchant_group_id) references custom_merchant_group(id),
      constraint fk_custom_merchant_group_merchant_merchant_id foreign key(merchant_id) references merchant(id),
      constraint uq_custom_merchant_group_merchant unique(
        custom_merchant_group_id,
        merchant_id
      )
    );

create
  table
    user_psp_scope(
      id bigint not null primary key,
      version int4 not null,
      user_id bigint not null,
      psp_id bigint not null,
      constraint fk_user_psp_scope_user_id foreign key(user_id) references "user"(id),
      constraint fk_user_psp_scope_psp_id foreign key(psp_id) references psp(id),
      constraint uq_user_psp_scope unique(
        user_id,
        psp_id
      )
    );

create
  table
    user_merchant_group_scope(
      id bigint not null primary key,
      version int4 not null,
      user_id bigint not null,
      merchant_group_id bigint not null,
      constraint fk_user_merchant_group_scope_merchant_user_id foreign key(user_id) references "user"(id),
      constraint fk_user_merchant_group_scope_merchant_group_id foreign key(merchant_group_id) references merchant_group(id),
      constraint uq_user_merchant_group_scope unique(
        user_id,
        merchant_group_id
      )
    );

create
  table
    user_merchant_scope(
      id bigint not null primary key,
      version int4 not null,
      user_id bigint not null,
      merchant_id bigint not null,
      constraint fk_user_merchant_scope_user_id foreign key(user_id) references "user"(id),
      constraint fk_user_merchant_scope_merchant_id foreign key(merchant_id) references merchant(id),
      constraint uq_user_merchant_scope unique(
        user_id,
        merchant_id
      )
    );

create
  table
    user_custom_merchant_group_scope(
      id bigint not null primary key,
      version int4 not null,
      user_id bigint not null,
      custom_merchant_group_id bigint not null,
      constraint fk_user_custom_merchant_group_scope_user_id foreign key(user_id) references "user"(id),
      constraint fk_user_custom_merchant_group_scope_custom_merchant_group_id foreign key(custom_merchant_group_id) references custom_merchant_group(id),
      constraint uq_user_custom_merchant_group_scope unique(
        user_id,
        custom_merchant_group_id
      )
    );
