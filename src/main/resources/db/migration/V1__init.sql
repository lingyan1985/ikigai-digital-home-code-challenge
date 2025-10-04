CREATE TABLE time_deposits
(
    id        INT PRIMARY KEY,
    plan_type VARCHAR(64)    NOT NULL,
    days      INT            NOT NULL,
    balance   DECIMAL(19, 2) NOT NULL
);

CREATE TABLE withdrawals
(
    id              INT PRIMARY KEY,
    time_deposit_id INT            NOT NULL,
    amount          DECIMAL(19, 2) NOT NULL,
    date            DATE           NOT NULL,
    CONSTRAINT fk_td FOREIGN KEY (time_deposit_id) REFERENCES time_deposits (id)
);


INSERT INTO time_deposits (id, plan_type, days, balance)
VALUES (1, 'basic', 60, 1000.00),
       (2, 'student', 120, 2000.00),
       (3, 'premium', 50, 5000.00),
       (4, 'student', 370, 3000.00), -- >1y: no student interest
       (5, 'basic', 20, 900.00); -- <30 days: no interest for any

INSERT INTO withdrawals (id, time_deposit_id, amount, date)
VALUES (10, 1, 50.00, CURRENT_DATE),
       (11, 3, 100.00, CURRENT_DATE);
