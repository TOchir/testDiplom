CREATE TABLE appUser (
                        id SERIAL PRIMARY KEY,
                        email VARCHAR(255),
                        password VARCHAR(255)
);

CREATE TABLE fileCloud (
                      id SERIAL PRIMARY KEY,
                      filename VARCHAR(255),
                      user_id INT NOT NULL,
                      FOREIGN KEY (user_id) REFERENCES user(id),
                      date TIMESTAMP,
                      size BIGINT
);
