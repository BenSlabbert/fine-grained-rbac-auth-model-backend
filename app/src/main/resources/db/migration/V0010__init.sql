CREATE
  SEQUENCE id_sequence
START WITH
  1 INCREMENT BY 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE
  TABLE
    psp(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE
  TABLE
    merchant(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE,
      psp_id BIGINT,
      merchant_group_id BIGINT,
      CONSTRAINT fk_merchant_psp FOREIGN KEY(psp_id) REFERENCES psp(id),
      CONSTRAINT fk_merchant_group_psp FOREIGN KEY(merchant_group_id) REFERENCES merchant_group(id),
      CONSTRAINT ck_psp_or_merchant_group CHECK(
        (
          psp_id IS NOT NULL
          AND merchant_group_id IS NULL
        )
        OR(
          psp_id IS NULL
          AND merchant_group_id IS NOT NULL
        )
        OR(
          psp_id IS NULL
          AND merchant_group_id IS NULL
        )
      )
    );

CREATE
  TABLE
    merchant_group(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE
  TABLE
    application(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE
  TABLE
    permission(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      application_id BIGINT NOT NULL,
      value VARCHAR(255) NOT NULL UNIQUE,
      CONSTRAINT fk_permission_application FOREIGN KEY(application_id) REFERENCES application(id)
    );

CREATE
  TABLE
    "user"(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE
  TABLE
    "role"(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE
  TABLE
    role_permission(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      role_id BIGINT NOT NULL,
      permission_id BIGINT NOT NULL,
      CONSTRAINT fk_role_permission_role FOREIGN KEY(role_id) REFERENCES "role"(id),
      CONSTRAINT fk_role_permission_permission FOREIGN KEY(permission_id) REFERENCES permission(id),
      CONSTRAINT uq_role_permission UNIQUE(
        role_id,
        permission_id
      )
    );

CREATE
  TABLE
    user_role(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      user_id BIGINT NOT NULL,
      role_id BIGINT NOT NULL,
      CONSTRAINT fk_user_role_user FOREIGN KEY(user_id) REFERENCES "user"(id),
      CONSTRAINT fk_user_role_role FOREIGN KEY(role_id) REFERENCES "role"(id),
      CONSTRAINT uq_user_role UNIQUE(
        user_id,
        role_id
      )
    );

CREATE
  TABLE
    custom_merchant_group(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE
  TABLE
    custom_merchant_group_merchant(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      custom_merchant_group_id BIGINT NOT NULL,
      merchant_id BIGINT NOT NULL,
      CONSTRAINT fk_custom_merchant_group_merchant_custom_merchant_group_id FOREIGN KEY(custom_merchant_group_id) REFERENCES custom_merchant_group(id),
      CONSTRAINT fk_custom_merchant_group_merchant_merchant_id FOREIGN KEY(merchant_id) REFERENCES merchant(id),
      CONSTRAINT uq_custom_merchant_group_merchant UNIQUE(
        custom_merchant_group_id,
        merchant_id
      )
    );

CREATE
  TABLE
    user_psp_scope(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      user_id BIGINT NOT NULL,
      psp_id BIGINT NOT NULL,
      CONSTRAINT fk_user_psp_scope_psp_id FOREIGN KEY(psp_id) REFERENCES psp(id),
      CONSTRAINT uq_user_role UNIQUE(
        user_id,
        psp_id
      )
    );

CREATE
  TABLE
    user_merchant_group_scope(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      user_id BIGINT NOT NULL,
      merchant_group_id BIGINT NOT NULL,
      CONSTRAINT fk_user_merchant_group_scope_merchant_group_id FOREIGN KEY(merchant_group_id) REFERENCES merchant_group(id),
      CONSTRAINT uq_user_role UNIQUE(
        user_id,
        merchant_group_id
      )
    );

CREATE
  TABLE
    user_merchant_scope(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      user_id BIGINT NOT NULL,
      merchant_id BIGINT NOT NULL,
      CONSTRAINT fk_user_merchant_scope_merchant_id FOREIGN KEY(merchant_id) REFERENCES merchant(id),
      CONSTRAINT uq_user_role UNIQUE(
        user_id,
        merchant_id
      )
    );
