CREATE TYPE Transaction AS(
    id INT,
    user_id INT,
	amount MONEY
);

-- DROP FUNCTION IF EXISTS  uspCreateTransaction;
CREATE OR REPLACE FUNCTION uspCreateTransaction(userid int, value money)
RETURNS Transaction
LANGUAGE plpgsql
AS $$
DECLARE
    new_transaction Transaction;
BEGIN
INSERT INTO transactions (user_id, amount) VALUES (userid,value)
RETURNING id, user_id, amount INTO new_transaction;
UPDATE users
SET is_premium = true
WHERE id=userid;
RETURN new_transaction;
END$$;