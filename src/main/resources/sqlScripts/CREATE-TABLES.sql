----  TABLESPACE pg_default;
create table if not exists company
(
  id             bigserial not null
    constraint company_pkey
      primary key,
  account_number varchar(255),
  address        varchar(255),
  email          varchar(255),
  name           varchar(255),
  phone_number   varchar(255),
  tax_id         varchar(255)
);

create table if not exists  invoice
(
  id          bigserial not null
    constraint invoice_pkey
      primary key,
  due_date    date,
  issued_date date,
  number      varchar(255),
  buyer_id    bigint
    constraint fk1qyhm8jdwg4pq2r0bxiwu9dji
      references company,
  seller_id   bigint
    constraint fkc9x0r4usl0d7k2k2lww1304cn
      references company
);


create table if not exists invoice_entry
(
 id          bigserial not null
   constraint invoice_entry_pkey
     primary key,
 description varchar(255),
 gross_value numeric(19, 2),
 net_value   numeric(19, 2),
 price       numeric(19, 2),
 quantity    bigint,
 vat_rate    numeric(3,2)
);


create table if not exists invoice_entries
(
  invoice_id bigint not null
    constraint fk5q7x7droydmmr4ut9h2tmkykr
      references invoice ON DELETE CASCADE,
  entries_id bigint not null
    constraint fke06o58iirxorpfhmvhu46opa4
      references invoice_entry ON DELETE CASCADE
);
