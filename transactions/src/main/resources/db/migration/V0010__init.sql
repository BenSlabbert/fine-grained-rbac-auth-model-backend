create
  sequence id_sequence
start with
  1 increment by 1 maxvalue 9223372036854775807 CACHE 1;

create
  table
    merchant(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null unique
    );

create
  table
    customer(
      id bigint not null primary key,
      version int4 not null,
      name varchar(255) not null unique
    );

create
  type payment_type as ENUM(
    'credit_card',
    'virtual_card',
    'sepa'
  );

create
  table
    instrument(
      id bigint not null primary key,
      version int4 not null,
      payment_type payment_type not null,
      name varchar(255) not null unique
    );

create
  type transaction_status as ENUM(
    'INITIATED',
    'COMPLETED',
    'FAILED'
  );

create
  table
    transaction(
      id bigint not null primary key,
      version int4 not null,
      transaction_status transaction_status not null,
      amount_in_cents int8 not null,
      customer_id bigint not null,
      instrument_id bigint not null,
      constraint fk_transaction_customer_id foreign key(customer_id) references customer(id),
      constraint fk_transaction_instrument_id foreign key(instrument_id) references instrument(id)
    );
