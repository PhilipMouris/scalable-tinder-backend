---------------- BANS PROCEDURES

-- DROP TYPE IF Exists "banData"; 
CREATE TYPE "banData" AS(
	id int,
	moderator_id int, 
	user_id int,
	reason varchar,
	created_At timestamp,
	expiry_date date);
DROP FUNCTION IF EXISTS "uspCreateBan";
CREATE OR REPLACE FUNCTION "uspCreateBan"(mod_id int,user_id int,in_reason varchar(400),in_expiry_date date) RETURNS "banData"
LANGUAGE 'plpgsql'
AS $$
DECLARE 
	temp_data "banData";
BEGIN
	INSERT INTO public.bans(moderator_id,user_id,reason,expiry_date) VALUES (mod_id,user_id,in_reason,in_expiry_date)
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;$$
;

DROP FUNCTION IF EXISTS "uspUpdateBan";
CREATE OR REPLACE FUNCTION "uspUpdateBan"(in_id int,mod_id int,user_id int,in_reason varchar(400),in_expiry_date date) RETURNS "banData"
LANGUAGE 'plpgsql'
AS $$
DECLARE 
	temp_data "banData";
BEGIN
	UPDATE public.bans SET 
	reason = COALESCE(in_reason,bans.reason),
	expiry_date = COALESCE(in_expiry_date,bans.expiry_date)
--  	user_id = COALESCE(user_id,bans.user_id),
-- 	moderator_id = COALESCE(mod_id,bans.moderator_id)
	WHERE public.bans.id = in_id
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;$$
;

DROP FUNCTION IF EXISTS "uspReadModeratorUserBans";
CREATE OR REPLACE FUNCTION "uspReadModeratorUserBans"(mod_id int,user_id int,page int,"limit" int) RETURNS TABLE(id int,moderator_id int,usr_id int,reason varchar,expiry_date date)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE public.bans.moderator_id = mod_id AND public.bans.user_id = "uspReadModeratorUserBans".user_id
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspReadAllBans";
CREATE OR REPLACE FUNCTION "uspReadAllBans"() RETURNS TABLE(id int,moderator_id int,user_id int,reason varchar,expiry_date date,page int,"limit" int)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspReadUserBans";
CREATE OR REPLACE FUNCTION "uspReadUserBans"(user_id int,page int,"limit" int) RETURNS TABLE(id int,moderator_id int,usr_id int,reason varchar,expiry_date date)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE public.bans.user_id = "uspReadUserBans".user_id
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspReadModeratorBans";
CREATE OR REPLACE FUNCTION "uspReadModeratorBans"(mod_id int,page int,"limit" int) RETURNS TABLE(id int,moderator_id int,user_id int,reason varchar,expiry_date date)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE bans.moderator_id = mod_id
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspReadBan";
CREATE OR REPLACE FUNCTION "uspReadBan"(in_id int) RETURNS TABLE(id int,moderator_id int,user_id int,reason varchar,expiry_date date)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE bans.id = in_id;
END;$$
;
DROP FUNCTION IF EXISTS "uspDeleteBan";
CREATE OR REPLACE FUNCTION "uspDeleteBan"(in_id int) RETURNS INT
LANGUAGE 'plpgsql'
AS $$
DECLARE 
	deleted_id INT;
BEGIN
	DELETE FROM public.bans 
	WHERE public.bans.id = in_id
	RETURNING public.bans.id INTO deleted_id;
	RETURN deleted_id;
END;$$

-------------------------------------

-------------------- Transactions Procedures
;
-- DROP TYPE IF Exists "transactionData"; 
CREATE TYPE "transactionData" AS(
	in_id int,
	in_user_id int, 
	in_amout money, 
	in_created_at timestamp);
	
DROP FUNCTION IF EXISTS "uspUpdateTransaction";
CREATE OR REPLACE FUNCTION "uspUpdateTransaction"(in_id int,in_user_id int, in_amout money) RETURNS "transactionData"
LANGUAGE 'plpgsql'
AS $$
DECLARE 
	temp_data "transactionData";
BEGIN
	UPDATE public.transactions SET 
	amount = COALESCE(in_amout,transactions.amount),
	user_id = COALESCE(in_user_id,transactions.user_id)
	WHERE public.transactions.id = in_id
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;$$
;
DROP FUNCTION IF EXISTS "uspReadAllTransactions";
CREATE OR REPLACE FUNCTION "uspReadAllTransactions"(page int,"limit" int) RETURNS TABLE(id int,user_id int,amount money,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT transactions.id,transactions.user_id,transactions.amount,transactions.created_at from public.transactions
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspReadAllUserTransactions";
CREATE OR REPLACE FUNCTION "uspReadAllUserTransactions"(in_user_id int,page int,"limit" int) RETURNS TABLE(id int,user_id int,amount money,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT transactions.id,transactions.user_id,transactions.amount,transactions.created_at from public.transactions
	WHERE transactions.user_id = in_user_id
	LIMIT "limit"
	OFFSET page*"limit";
	
END;$$
;
DROP FUNCTION IF EXISTS "uspReadTransaction";
CREATE OR REPLACE FUNCTION "uspReadTransaction"(in_id int) RETURNS TABLE(id int,user_id int,amount money,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT transactions.id,transactions.user_id,transactions.amount,transactions.created_at from public.transactions
	WHERE transactions.id = in_id;
END;$$
;
DROP FUNCTION IF EXISTS "uspDeleteTransaction";
CREATE OR REPLACE FUNCTION "uspDeleteTransaction"(in_id int) RETURNS INT
LANGUAGE 'plpgsql'
AS $$
DECLARE
    deleted_id INT;
BEGIN
	DELETE FROM public.transactions 
	WHERE public.transactions.id = in_id
	RETURNING public.transactions.id INTO deleted_id;
	RETURN deleted_id;
END;$$
;
-- ----------------- BLOCKS Procedures

-- DROP TYPE IF Exists "blockData"; 
CREATE TYPE "blockData" AS(
	in_source int,
	in_target int, 
	in_created_at timestamp);
	
-- CREATE OR REPLACE FUNCTION "uspUpdateBlock"(in_source int,in_target int, in_created_at timestamp) RETURNS "blockData"
-- LANGUAGE 'plpgsql'
-- AS $$
-- DECLARE 
-- 	temp_data "blockData";
-- BEGIN
-- 	UPDATE public.blocks SET 
-- 	created_at = COALESCE(in_created_at,public.blocks.created_at)
-- 	WHERE public.blocks.source_user_id = in_source AND public.blocks.target_user_id = in_target
-- 	RETURNING * INTO temp_data;
-- 	RETURN temp_data;
-- END;$$
-- ;

DROP FUNCTION IF EXISTS "uspReadAllBlocks";
CREATE OR REPLACE FUNCTION "uspReadAllBlocks"(page int,"limit" int) RETURNS TABLE(source_user_id int,target_user_id int,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT blocks.source_user_id,blocks.target_user_id,blocks.created_at from public.blocks
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspReadSourceBlocks";
CREATE OR REPLACE FUNCTION "uspReadSourceBlocks"(in_source_user int,page int,"limit" int) RETURNS TABLE(source_user_id int,target_user_id int,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT blocks.source_user_id,blocks.target_user_id,blocks.created_at from public.blocks
	WHERE blocks.source_user_id = in_source_user
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;

DROP FUNCTION IF EXISTS "uspReadTargetBlocks";
CREATE OR REPLACE FUNCTION "uspReadTargetBlocks"(in_target_user int,page int,"limit" int) RETURNS TABLE(source_user_id int,target_user_id int,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT blocks.source_user_id,blocks.target_user_id,blocks.created_at from public.blocks
	WHERE blocks.target_user_id = in_target_user
	LIMIT "limit"
	OFFSET page*"limit";
END;$$
;
DROP FUNCTION IF EXISTS "uspDeleteBlock";
CREATE OR REPLACE FUNCTION "uspDeleteBlock"(in_source int,in_target int) RETURNS "blockData"
LANGUAGE 'plpgsql'
AS $$
DECLARE 
	temp_data "blockData";
BEGIN
	DELETE FROM public.blocks 
	WHERE public.blocks.source_user_id = in_source AND public.blocks.target_user_id = in_target
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;$$
;