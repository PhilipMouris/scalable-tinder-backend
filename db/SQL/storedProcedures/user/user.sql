
DROP TYPE IF EXISTS userPublicData;
CREATE TYPE userPublicData AS(
    id INT,
	email VARCHAR,
	first_name VARCHAR,
	last_name VARCHAR

);

DROP TYPE IF EXISTS userProfile;
CREATE TYPE userProfile AS(
    id INT,
	email VARCHAR,
	first_name VARCHAR,
	last_name VARCHAR,
	is_premium BOOL

);

DROP TYPE IF EXISTS userAuthenticationData;
CREATE TYPE userAuthenticationData AS(
    id INT,
	email VARCHAR,
	password VARCHAR
);


DROP FUNCTION IF EXISTS  uspSignUp;
CREATE OR REPLACE FUNCTION uspSignUp(
	_email VARCHAR(200),
	"_password" VARCHAR(200),
	_first_name VARCHAR(200),
	_last_name  VARCHAR(200)
) RETURNS userPublicData
LANGUAGE 'plpgsql'
AS $$
DECLARE
   new_user userPublicData;
BEGIN
	INSERT INTO public.users(email, "password", first_name, last_name) VALUES
	(_email,"_password", _first_name, _last_name)
	 RETURNING id, email, first_name, last_name INTO new_user;
	 RETURN new_user;
END;$$
;

-- SELECT * FROM uspSignUp('streakfull@gmail.com','zaq12wsx','youssef','sherif')

DROP  FUNCTION  IF EXISTS  uspLogin;
CREATE OR REPLACE FUNCTION uspLogin(
	_email VARCHAR(200)
) RETURNS userAuthenticationData
LANGUAGE 'plpgsql'
AS $$
DECLARE
    authenticated_user userAuthenticationData;
BEGIN
	SELECT id, email, password From public."users" AS u WHERE u.email = _email
	INTO authenticated_user;
	RETURN authenticated_user;
END;$$
;

-- SELECT * FROM uspLogin('streakfull@gmail.com', 'zaq12wsxz');



DROP  FUNCTION  IF EXISTS  uspDeleteProfile;
CREATE OR REPLACE FUNCTION uspDeleteProfile(
	_id INT
) RETURNS INT
LANGUAGE 'plpgsql'
AS $$
DECLARE
    deleted_id INT;
BEGIN
	DELETE FROM public."users" as u WHERE u.id = _id
	RETURNING u.id INTO deleted_id;
	RETURN deleted_id;
END;$$
;


-- SELECT * FROM uspDeleteProfile(17);



DROP  FUNCTION  IF EXISTS  uspEditAccountData;
CREATE OR REPLACE FUNCTION uspEditAccountData(
	_id int,
	_email VARCHAR(200),
	_first_name VARCHAR(200),
	_last_name VARCHAR(200)
) RETURNS userPublicData
LANGUAGE 'plpgsql'
AS $$
DECLARE
    updated_user userPublicData;
BEGIN
	UPDATE public.users AS u SET
	 email = COALESCE(_email, u.email),
	 first_name = COALESCE(_first_name, u.first_name),
	 last_name = COALESCE(_last_name, u.last_name)
	 WHERE u.id = _id
	 RETURNING id, email, first_name, last_name INTO updated_user;
	 RETURN updated_user;
END;$$
;


--SELECT * FROM uspEditAccountData(18,'test@gmail.com', null,'updateddd');

DROP  FUNCTION  IF EXISTS  uspEditPassword;
CREATE OR REPLACE FUNCTION uspEditPassword(
    _id int,
	old_password VARCHAR(200),
	new_password VARCHAR(200)
) RETURNS userPublicData
LANGUAGE 'plpgsql'
AS $$
DECLARE
    updated_user userPublicData;
BEGIN
	UPDATE public.users AS u SET
	 password = new_password
	 WHERE u.id = _id AND u.password = old_password
	 RETURNING id, email, first_name, last_name INTO updated_user;
	 RETURN updated_user;
END;$$
;

--SELECT * FROM uspEditPassword(18,'psps','sss');


DROP  FUNCTION  IF EXISTS  uspViewProfile;
CREATE OR REPLACE FUNCTION uspViewProfile(
	_id int
) RETURNS userProfile
LANGUAGE 'plpgsql'
AS $$
DECLARE
    user_profile userProfile;
BEGIN
	SELECT id, email, first_name, last_name,is_premium From public."users" AS u
	WHERE id = _id
	INTO user_profile;
	RETURN user_profile;
END;$$
;

