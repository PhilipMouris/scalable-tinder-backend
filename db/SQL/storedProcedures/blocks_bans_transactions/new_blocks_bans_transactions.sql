-- FUNCTION: public.uspReadModeratorBans(integer, integer, integer)

-- DROP FUNCTION public."uspReadModeratorBans"(integer, integer, integer);
CREATE TYPE "banData" AS(
	id int,
	moderator_id int, 
	user_id int,
	reason varchar,
	created_At timestamp,
	expiry_date date);
	
-- DROP TYPE IF Exists "transactionData"; 
CREATE TYPE "transactionData" AS(
	in_id int,
	in_user_id int, 
	in_amout money, 
	in_created_at timestamp);
	
-- DROP TYPE IF Exists "blockData"; 
CREATE TYPE "blockData" AS(
	in_source int,
	in_target int, 
	in_created_at timestamp);
	
	
CREATE OR REPLACE FUNCTION public."uspReadModeratorBans"(
	mod_id integer,
	page integer,
	"limit" integer)
    RETURNS TABLE(id integer, moderator_id integer, user_id integer, reason character varying, expiry_date date) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE bans.moderator_id = mod_id
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadModeratorBans"(integer, integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadUserBans(integer, integer, integer)

-- DROP FUNCTION public."uspReadUserBans"(integer, integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadUserBans"(
	user_id integer,
	page integer,
	"limit" integer)
    RETURNS TABLE(id integer, moderator_id integer, usr_id integer, reason character varying, expiry_date date) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE public.bans.user_id = "uspReadUserBans".user_id
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadUserBans"(integer, integer, integer)
    OWNER TO postgres;
	
-- FUNCTION: public.uspUpdateBan(integer, integer, integer, character varying, date)

-- DROP FUNCTION public."uspUpdateBan"(integer, integer, integer, character varying, date);

CREATE OR REPLACE FUNCTION public."uspUpdateBan"(
	in_id integer,
	mod_id integer,
	user_id integer,
	in_reason character varying,
	in_expiry_date date)
    RETURNS "banData"
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
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
END;
$BODY$;

ALTER FUNCTION public."uspUpdateBan"(integer, integer, integer, character varying, date)
    OWNER TO postgres;

-- FUNCTION: public.uspReadBan(integer)

-- DROP FUNCTION public."uspReadBan"(integer);

CREATE OR REPLACE FUNCTION public."uspReadBan"(
	in_id integer)
    RETURNS TABLE(id integer, moderator_id integer, user_id integer, reason character varying, expiry_date date) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE bans.id = in_id;
END;
$BODY$;

ALTER FUNCTION public."uspReadBan"(integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadAllBans()

-- DROP FUNCTION public."uspReadAllBans"();

CREATE OR REPLACE FUNCTION public."uspReadAllBans"(
	)
    RETURNS TABLE(id integer, moderator_id integer, user_id integer, reason character varying, expiry_date date, page integer, "limit" integer) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadAllBans"()
    OWNER TO postgres;


-- FUNCTION: public.uspDeleteBan(integer)

-- DROP FUNCTION public."uspDeleteBan"(integer);

CREATE OR REPLACE FUNCTION public."uspDeleteBan"(
	in_id integer)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE 
	deleted_id INT;
BEGIN
	DELETE FROM public.bans 
	WHERE public.bans.id = in_id
	RETURNING public.bans.id INTO deleted_id;
	RETURN deleted_id;
END;
$BODY$;

ALTER FUNCTION public."uspDeleteBan"(integer)
    OWNER TO postgres;

-- FUNCTION: public.uspCreateBan(integer, integer, character varying, date)

-- DROP FUNCTION public."uspCreateBan"(integer, integer, character varying, date);

CREATE OR REPLACE FUNCTION public."uspCreateBan"(
	mod_id integer,
	user_id integer,
	in_reason character varying,
	in_expiry_date date)
    RETURNS "banData"
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE 
	temp_data "banData";
BEGIN
	INSERT INTO public.bans(moderator_id,user_id,reason,expiry_date) VALUES (mod_id,user_id,in_reason,in_expiry_date)
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;
$BODY$;

ALTER FUNCTION public."uspCreateBan"(integer, integer, character varying, date)
    OWNER TO postgres;

-- FUNCTION: public.uspReadModeratorUserBans(integer, integer, integer, integer)

-- DROP FUNCTION public."uspReadModeratorUserBans"(integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadModeratorUserBans"(
	mod_id integer,
	user_id integer,
	page integer,
	"limit" integer)
    RETURNS TABLE(id integer, moderator_id integer, usr_id integer, reason character varying, expiry_date date) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT bans.id,bans.moderator_id,bans.user_id,bans.reason,bans.expiry_date from public.bans
	WHERE public.bans.moderator_id = mod_id AND public.bans.user_id = "uspReadModeratorUserBans".user_id
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadModeratorUserBans"(integer, integer, integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspUpdateTransaction(integer, integer, money)

-- DROP FUNCTION public."uspUpdateTransaction"(integer, integer, money);

CREATE OR REPLACE FUNCTION public."uspUpdateTransaction"(
	in_id integer,
	in_user_id integer,
	in_amout money)
    RETURNS "transactionData"
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE 
	temp_data "transactionData";
BEGIN
	UPDATE public.transactions SET 
	amount = COALESCE(in_amout,transactions.amount),
	user_id = COALESCE(in_user_id,transactions.user_id)
	WHERE public.transactions.id = in_id
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;
$BODY$;

ALTER FUNCTION public."uspUpdateTransaction"(integer, integer, money)
    OWNER TO postgres;

-- FUNCTION: public.uspReadAllTransactions(integer, integer)

-- DROP FUNCTION public."uspReadAllTransactions"(integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadAllTransactions"(
	page integer,
	"limit" integer)
    RETURNS TABLE(id integer, user_id integer, amount money, created_at timestamp without time zone) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT transactions.id,transactions.user_id,transactions.amount,transactions.created_at from public.transactions
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadAllTransactions"(integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadAllUserTransactions(integer, integer, integer)

-- DROP FUNCTION public."uspReadAllUserTransactions"(integer, integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadAllUserTransactions"(
	in_user_id integer,
	page integer,
	"limit" integer)
    RETURNS TABLE(id integer, user_id integer, amount money, created_at timestamp without time zone) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT transactions.id,transactions.user_id,transactions.amount,transactions.created_at from public.transactions
	WHERE transactions.user_id = in_user_id
	LIMIT "limit"
	OFFSET page*"limit";
	
END;
$BODY$;

ALTER FUNCTION public."uspReadAllUserTransactions"(integer, integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadTransaction(integer)

-- DROP FUNCTION public."uspReadTransaction"(integer);

CREATE OR REPLACE FUNCTION public."uspReadTransaction"(
	in_id integer)
    RETURNS TABLE(id integer, user_id integer, amount money, created_at timestamp without time zone) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT transactions.id,transactions.user_id,transactions.amount,transactions.created_at from public.transactions
	WHERE transactions.id = in_id;
END;
$BODY$;

ALTER FUNCTION public."uspReadTransaction"(integer)
    OWNER TO postgres;

-- FUNCTION: public.uspDeleteTransaction(integer)

-- DROP FUNCTION public."uspDeleteTransaction"(integer);

CREATE OR REPLACE FUNCTION public."uspDeleteTransaction"(
	in_id integer)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
    deleted_id INT;
BEGIN
	DELETE FROM public.transactions 
	WHERE public.transactions.id = in_id
	RETURNING public.transactions.id INTO deleted_id;
	RETURN deleted_id;
END;
$BODY$;

ALTER FUNCTION public."uspDeleteTransaction"(integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadAllBlocks(integer, integer)

-- DROP FUNCTION public."uspReadAllBlocks"(integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadAllBlocks"(
	page integer,
	"limit" integer)
    RETURNS TABLE(source_user_id integer, target_user_id integer, created_at timestamp without time zone) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT blocks.source_user_id,blocks.target_user_id,blocks.created_at from public.blocks
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadAllBlocks"(integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadSourceBlocks(integer, integer, integer)

-- DROP FUNCTION public."uspReadSourceBlocks"(integer, integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadSourceBlocks"(
	in_source_user integer,
	page integer,
	"limit" integer)
    RETURNS TABLE(source_user_id integer, target_user_id integer, created_at timestamp without time zone) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT blocks.source_user_id,blocks.target_user_id,blocks.created_at from public.blocks
	WHERE blocks.source_user_id = in_source_user
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadSourceBlocks"(integer, integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspReadTargetBlocks(integer, integer, integer)

-- DROP FUNCTION public."uspReadTargetBlocks"(integer, integer, integer);

CREATE OR REPLACE FUNCTION public."uspReadTargetBlocks"(
	in_target_user integer,
	page integer,
	"limit" integer)
    RETURNS TABLE(source_user_id integer, target_user_id integer, created_at timestamp without time zone) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
	RETURN QUERY
	SELECT blocks.source_user_id,blocks.target_user_id,blocks.created_at from public.blocks
	WHERE blocks.target_user_id = in_target_user
	LIMIT "limit"
	OFFSET page*"limit";
END;
$BODY$;

ALTER FUNCTION public."uspReadTargetBlocks"(integer, integer, integer)
    OWNER TO postgres;

-- FUNCTION: public.uspDeleteBlock(integer, integer)

-- DROP FUNCTION public."uspDeleteBlock"(integer, integer);

CREATE OR REPLACE FUNCTION public."uspDeleteBlock"(
	in_source integer,
	in_target integer)
    RETURNS "blockData"
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE 
	temp_data "blockData";
BEGIN
	DELETE FROM public.blocks 
	WHERE public.blocks.source_user_id = in_source AND public.blocks.target_user_id = in_target
	RETURNING * INTO temp_data;
	RETURN temp_data;
END;
$BODY$;

ALTER FUNCTION public."uspDeleteBlock"(integer, integer)
    OWNER TO postgres;
