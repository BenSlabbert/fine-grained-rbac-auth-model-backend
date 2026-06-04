CREATE
  SEQUENCE id_sequence
START WITH
  1 INCREMENT BY 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE
  TABLE
    psp(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL
    );

CREATE
  TABLE
    merchant(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL,
      psp_id BIGINT,
      CONSTRAINT fk_merchant_psp FOREIGN KEY(psp_id) REFERENCES psp(id)
    );

CREATE
  TABLE
    merchant_group(
      id BIGINT NOT NULL PRIMARY KEY,
      version int4 NOT NULL
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
