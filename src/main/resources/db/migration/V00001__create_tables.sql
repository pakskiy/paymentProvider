CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SEQUENCE id_merchant_seq;
CREATE SEQUENCE id_account_seq;
CREATE SEQUENCE id_transaction_seq;
CREATE SEQUENCE id_notification_seq;

CREATE TABLE countries (
    id varchar(3) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Each merchant can have one or more accounts
CREATE TABLE merchants
(
    id bigint NOT NULL DEFAULT nextval('id_merchant_seq'),
    login varchar(255) NOT NULL,
    key varchar(255) NOT NULL,
    created_at timestamp NOT NULL default NOW(),
    updated_at timestamp,
    status varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_merchants_login ON merchants (login);

CREATE TABLE accounts
(
    id bigint NOT NULL DEFAULT nextval('id_account_seq'),
    merchant_id bigint NOT NULL,
    deposit_amount bigint NOT NULL,
    limit_amount bigint NOT NULL,
    is_overdraft smallint,
    created_at timestamp NOT NULL default NOW(),
    updated_at timestamp,
    PRIMARY KEY (id),
    CONSTRAINT fk_merchants_accounts FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

CREATE UNIQUE INDEX uq_accounts_merchant_id ON accounts (merchant_id);

CREATE TABLE currencies (
    id varchar(3) NOT NULL,
    code varchar(3) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE languages (
    id varchar(2) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Each transaction link with one account by id
CREATE TABLE transactions
(
    id bigint NOT NULL DEFAULT nextval('id_transaction_seq'),
    method varchar(10) NOT NULL,
    amount bigint NOT NULL,
    account_id bigint NOT NULL,
    currency_id varchar(3) NOT NULL,
    provider_transaction_id varchar(100) NOT NULL,
    card_data varchar(2000) NOT NULL,
    language_id varchar(2),
    notification_url varchar(2000),
    customer_data varchar(2000) NOT NULL,
    type varchar(15) NOT NULL,
    created_at timestamp NOT NULL default NOW(),
    updated_at timestamp,
    status varchar(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_languages_transactions FOREIGN KEY (language_id) REFERENCES languages (id),
    CONSTRAINT fk_currencies_transactions FOREIGN KEY (currency_id) REFERENCES currencies (id),
    CONSTRAINT fk_accounts_transactions FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE notifications (
    id bigint NOT NULL DEFAULT nextval('id_notification_seq'),
    transaction_id bigint NOT NULL,
    url varchar(255) NOT NULL,
    response varchar(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_notifications FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);

CREATE UNIQUE INDEX uq_notifications_tr_id ON notifications (transaction_id);