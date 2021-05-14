-- DROP TYPE IF Exists moderatorInfo; 
CREATE TYPE moderatorInfo AS(
	"id" INT,
	email VARCHAR(200)
);

-- DROP TYPE IF EXISTS moderatorAuthenticationInfo;
CREATE TYPE moderatorAuthenticationInfo AS(
    id INT,
	email VARCHAR,
	password VARCHAR
);

-- DROP FUNCTION  IF Exists uspModSignUp;
CREATE OR REPLACE FUNCTION uspModSignUp(
    _email VARCHAR,
    _password VARCHAR
    ) RETURNS moderatorInfo
LANGUAGE "plpgsql"
AS $$
DECLARE new_mod moderatorInfo;
BEGIN
    INSERT INTO public.moderators(email,"password")
    VALUES (_email, _password)
    RETURNING * INTO new_mod;
    RETURN new_mod;
END; $$
;
-- SELECT * FROM uspModSignUp('linaeweis2@gmail.com','lina');

-- DROP  FUNCTION  IF EXISTS  uspModLogin;
CREATE OR REPLACE FUNCTION uspModLogin(
	_email VARCHAR(200)
) RETURNS moderatorAuthenticationInfo
LANGUAGE 'plpgsql'
AS $$
DECLARE
    authenticated_moderator moderatorAuthenticationInfo;
BEGIN
	SELECT "id", email, password From public.moderators AS m WHERE m.email = _email
	INTO authenticated_moderator;
	RETURN authenticated_moderator;
END;$$
;
-- SELECT * FROM uspModLogin('linaeweis2@gmail.com');

-- DROP  FUNCTION  IF EXISTS  uspDeleteMod;
CREATE OR REPLACE FUNCTION uspDeleteMod(
	_id INT
) RETURNS INT
LANGUAGE 'plpgsql'
AS $$
DECLARE
    deleted_id INT;
BEGIN
	DELETE FROM public.moderators as m WHERE m.id = _id
	RETURNING m.id INTO deleted_id;
	RETURN deleted_id;
END;$$
;
-- SELECT * FROM uspDeleteMod(2);

-- DROP  FUNCTION  IF EXISTS  uspEditMod;
CREATE OR REPLACE FUNCTION uspEditMod(
	_id int,
	_email VARCHAR(200)
) RETURNS moderatorInfo
LANGUAGE 'plpgsql'
AS $$
DECLARE
    updated_moderator moderatorInfo;
BEGIN
	UPDATE public.moderators AS m SET
	 email = COALESCE(_email, m.email)
	 WHERE m.id = _id
	 RETURNING id, email INTO updated_moderator;
	 RETURN updated_moderator;
END;$$
;

-- SELECT * FROM uspEditMod(4,'test@gmail.com');

-- DROP  FUNCTION  IF EXISTS  uspEditModPassword;
CREATE OR REPLACE FUNCTION uspEditModPassword(
    _id int,
	old_password VARCHAR(200),
	new_password VARCHAR(200)
) RETURNS moderatorInfo
LANGUAGE 'plpgsql'
AS $$
DECLARE
    updated_moderator moderatorInfo;
BEGIN
	UPDATE public.moderators AS m SET
	 password = new_password
	 WHERE m.id = _id AND m.password = old_password
	 RETURNING moderatorInfo INTO updated_moderator;
	 RETURN updated_moderator;
END;$$
;

-- SELECT * FROM uspEditModPassword(4,'lina','test');

-- DROP  FUNCTION  IF EXISTS  uspViewMod;
CREATE OR REPLACE FUNCTION uspViewMod(
	_id int
) RETURNS moderatorInfo
LANGUAGE 'plpgsql'
AS $$
DECLARE
    moderator_profile moderatorInfo;
BEGIN
	SELECT id, email From public.moderators AS m 
	WHERE id = _id 
	INTO moderator_profile;
	RETURN moderator_profile;
END;$$
;

-- SELECT * FROM uspViewMod(4);

-- DROP  FUNCTION  IF EXISTS  uspViewReported;
CREATE OR REPLACE FUNCTION uspViewReported(page int,"limit" int) 
RETURNS TABLE("id" INT,target_user_id INT, reason VARCHAR(400))
LANGUAGE 'plpgsql'
AS $$
DECLARE
BEGIN
	RETURN QUERY
    SELECT reports.id, reports.target_user_id, reports.reason From public.reports 
	ORDER BY created_at LIMIT "limit" OFFSET page*"limit";
END;$$
;
-- SELECT * FROM uspViewReported(0,10)