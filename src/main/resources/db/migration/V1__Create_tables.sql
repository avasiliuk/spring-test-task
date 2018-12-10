CREATE TABLE products (
  id    UUID PRIMARY KEY   NOT NULL,
  name  VARCHAR_IGNORECASE NOT NULL,
  price DECIMAL(20,2)      NOT NULL
);

CREATE UNIQUE INDEX unique_products_name ON products(name);

CREATE TABLE orders (
  id          UUID PRIMARY KEY   NOT NULL,
  buyer_email VARCHAR_IGNORECASE NOT NULL,
  created     TIMESTAMP          NOT NULL
);

CREATE INDEX index_orders_created ON orders(created);

CREATE TABLE ordered_products (
  order_id      UUID          NOT NULL,
  product_id    UUID          NOT NULL,
  ordered_price DECIMAL(20,2) NOT NULL,
  PRIMARY KEY (order_id, product_id),
  CONSTRAINT fk_orders_id FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_products_id FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
);
