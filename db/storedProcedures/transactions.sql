CREATE PROCEDURE uspCreateTransaction(userid int, value money)
LANGUAGE plpgsql
AS $$
BEGIN
INSERT INTO transactions (user_id, amount) VALUES (userid,value);
UPDATE users
SET is_premium = true
WHERE id=userid;
END;$$;