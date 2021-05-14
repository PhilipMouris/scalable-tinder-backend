-- DROP TYPE IF Exists interestInfo; 
CREATE TYPE interestInfo AS(
	"id" INT,
	"name" VARCHAR(200)
);

-- DROP FUNCTION  IF Exists uspCreateInterest;
CREATE OR REPLACE FUNCTION uspCreateInterest(
    _name VARCHAR(200)
    ) RETURNS interestInfo
LANGUAGE "plpgsql"
AS $$
DECLARE new_interest interestInfo;
BEGIN
    INSERT INTO public.interests("name")
    VALUES (_name)
    RETURNING * INTO new_interest;
    RETURN new_interest;
END; $$
;
-- SELECT * FROM uspCreateInterest('HL');

-- DROP  FUNCTION  IF EXISTS  uspViewInterests;
CREATE OR REPLACE FUNCTION uspViewInterests(
	page int,"limit" int
) RETURNS TABLE("id" INT, "name" VARCHAR(200))
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT interests.id, interests.name::varchar(200) From public.interests 
	LIMIT "limit" OFFSET page*"limit";
END;$$
;
-- SELECT * FROM uspViewInterests(0,3);

-- DROP  FUNCTION  IF EXISTS  uspEditInterest;
CREATE OR REPLACE FUNCTION uspEditInterest(
	_id int,
	_name VARCHAR(200)
) RETURNS interestInfo
LANGUAGE 'plpgsql'
AS $$
DECLARE
    updated_interest interestInfo;
BEGIN
	UPDATE public.interests AS i SET
	 "name" = COALESCE(_name, i."name")
	 WHERE i.id = _id
	 RETURNING id, "name" INTO updated_interest;
	 RETURN updated_interest;
END;$$
;

-- SELECT * FROM uspEditInterest(4,'NCH');

-- DROP  FUNCTION  IF EXISTS  uspDeleteInterest;
CREATE OR REPLACE FUNCTION uspDeleteInterest(
	_id INT
) RETURNS INT
LANGUAGE 'plpgsql'
AS $$
DECLARE
    deleted_id INT;
BEGIN
	DELETE FROM public.interests as i WHERE i.id = _id
	RETURNING i.id INTO deleted_id;
	RETURN deleted_id;
END;$$
;
-- SELECT * FROM uspDeleteInterest(3);