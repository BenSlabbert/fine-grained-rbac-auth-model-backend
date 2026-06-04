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
      CONSTRAINT fk_merchant_psp FOREIGN KEY(psp_id) REFERENCES psp(id)
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
    merchant_group_merchant(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      merchant_group_id BIGINT NOT NULL,
      merchant_id BIGINT NOT NULL,
      CONSTRAINT fk_merchant_group_merchants_merchant_group FOREIGN KEY(merchant_group_id) REFERENCES merchant_group(id),
      CONSTRAINT fk_merchant_group_merchants_merchant FOREIGN KEY(merchant_id) REFERENCES merchant(id),
      CONSTRAINT uq_merchant_group_merchants_merchant_group_id_merchant_id UNIQUE(
        merchant_group_id,
        merchant_id
      )
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
      value VARCHAR(255) NOT NULL,
      CONSTRAINT fk_permission_application FOREIGN KEY(application_id) REFERENCES application(id),
      CONSTRAINT uq_permission_application_id_value UNIQUE(
        application_id,
        value
      )
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
