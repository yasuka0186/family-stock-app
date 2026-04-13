CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE family_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    invite_code VARCHAR(20) NOT NULL UNIQUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE family_memberships (
    id BIGSERIAL PRIMARY KEY,
    family_group_id BIGINT NOT NULL REFERENCES family_groups(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_family_memberships_group_user UNIQUE (family_group_id, user_id)
);

CREATE TABLE stock_items (
    id BIGSERIAL PRIMARY KEY,
    family_group_id BIGINT NOT NULL REFERENCES family_groups(id),
    name VARCHAR(120) NOT NULL,
    category VARCHAR(50),
    unit VARCHAR(20) NOT NULL DEFAULT '個',
    current_stock INTEGER NOT NULL DEFAULT 0 CHECK (current_stock >= 0),
    minimum_stock INTEGER NOT NULL DEFAULT 0 CHECK (minimum_stock >= 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_stock_items_group_name UNIQUE (family_group_id, name)
);

CREATE TABLE shopping_list_items (
    id BIGSERIAL PRIMARY KEY,
    family_group_id BIGINT NOT NULL REFERENCES family_groups(id),
    stock_item_id BIGINT REFERENCES stock_items(id),
    name_snapshot VARCHAR(120) NOT NULL,
    unit_snapshot VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    source_type VARCHAR(20) NOT NULL,
    note VARCHAR(255),
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_shopping_list_status CHECK (status IN ('PENDING', 'BOUGHT', 'SKIPPED')),
    CONSTRAINT chk_shopping_list_source_type CHECK (source_type IN ('AUTO_LOW_STOCK', 'MANUAL'))
);

CREATE UNIQUE INDEX ux_shopping_list_pending_stock_item
    ON shopping_list_items (family_group_id, stock_item_id)
    WHERE status = 'PENDING' AND stock_item_id IS NOT NULL;
