CREATE TABLE users (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
  --UNIQUE KEY user_email_unique (email)
);

CREATE TABLE transactions (
  id VARCHAR(255) PRIMARY KEY,
  amount DECIMAL NOT NULL,
  paymentmethod VARCHAR(255) NOT NULL,
  currency VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  user_id VARCHAR(255) NOT NULL,
  transactiontimestamp TIMESTAMP NOT NULL,
  FOREIGN KEY (user_id) references users(id)
  --UNIQUE KEY user_email_unique (email)
);