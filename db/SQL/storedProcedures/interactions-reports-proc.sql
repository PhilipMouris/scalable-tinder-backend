-- ---- INTERACTIONS PROCEDURES
DROP TYPE IF Exists interactionData; 
CREATE TYPE interactionData AS(
	id int,
	source_user_id int,
	target_user_id int,
	type interaction_type,
	created_at timestamp	
);
-- ---- INTERACTIONS PROCEDURES

CREATE OR REPLACE FUNCTION "uspReadInteraction"(interaction_id int) 
RETURNS TABLE(id int,source_user_id int,target_user_id int,type interaction_type,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT * from public.interactions
	WHERE public.interactions.id = interaction_id;
END;$$
;

CREATE OR REPLACE FUNCTION "uspReadInteractions"(page int,"limit" int) 
RETURNS TABLE(id int,source_user_id int,target_user_id int,type interaction_type,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT * from public.interactions
	LIMIT "limit" OFFSET page*"limit";
END;$$
;

CREATE OR REPLACE FUNCTION "uspReadUserSourceInteractions"(source_id int,page int,"limit" int) 
RETURNS TABLE(id int,source_user_id int,target_user_id int,type interaction_type,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT * from public.interactions
	WHERE public.interactions.source_user_id=source_id
	LIMIT "limit" OFFSET page*"limit";
END;$$
;

CREATE OR REPLACE FUNCTION "uspReadUserTargetInteractions"(target_id int,page int,"limit" int) 
RETURNS TABLE(id int,source_user_id int,target_user_id int,type interaction_type,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT * from public.interactions
	WHERE public.interactions.target_user_id=target_id
	LIMIT "limit" OFFSET page*"limit";
END;$$
;

CREATE OR REPLACE FUNCTION "uspUpdateInteraction"(
	int_id integer,
	source_id int DEFAULT NULL,
	target_id int DEFAULT NULL,
	int_type interaction_type DEFAULT NULL)
	RETURNS interactionData
    LANGUAGE 'plpgsql'
AS $$
DECLARE updated_interaction interactionData;
BEGIN
    UPDATE interactions
       SET source_user_id      = COALESCE(source_id, interactions.source_user_id),
           target_user_id      = COALESCE(target_id, interactions.target_user_id),
           "type"              = COALESCE(int_type, interactions.type)
     WHERE interactions.id = int_id
     RETURNING * INTO updated_interaction;
	 RETURN updated_interaction;
END;$$
;

CREATE OR REPLACE FUNCTION "uspDeleteInteraction"(interaction_id int)
RETURNS int
LANGUAGE 'plpgsql'
AS $$
DECLARE
	deleted_id INT;
BEGIN
	DELETE FROM public.interactions as i
	WHERE i.id = interaction_id
	RETURNING i.id INTO deleted_id;
	RETURN deleted_id;
END;$$
;

 CREATE OR REPLACE FUNCTION "uspCheckIfUsersMatched"(first_user_id int, second_user_id int)
 RETURNS TABLE(id int,source_user_id int,target_user_id int,type interaction_type,created_at timestamp)
 LANGUAGE 'plpgsql'
 AS $$
 BEGIN
 	RETURN QUERY
 	SELECT * from public.interactions
 	WHERE (public.interactions.source_user_id = first_user_id AND
 		   public.interactions.target_user_id = second_user_id AND
 		   NOT public.interactions.type = 'dislike') OR
 		   (public.interactions.source_user_id = second_user_id AND
 		   public.interactions.target_user_id = first_user_id AND
 		   NOT public.interactions.type = 'dislike');
 END;$$
 ;

------------ REPORTS PROCEDURES

-- DROP TYPE IF Exists reportData; 
-- CREATE TYPE reportData AS(
-- 	id int,
-- 	source_user_id int,
-- 	target_user_id int,
-- 	reason varchar(400),
-- 	created_at timestamp	
-- );

CREATE OR REPLACE FUNCTION "uspUpdateReport"(
	report_id integer,
	updated_reason varchar(400) DEFAULT NULL)
	RETURNS reportData
    LANGUAGE 'plpgsql'
AS $$
DECLARE updated_report reportData;
BEGIN
    UPDATE reports
       SET reason = COALESCE(updated_reason, reports.reason)
     WHERE reports.id = report_id
	 RETURNING * INTO updated_report;
     RETURN updated_report;
END;$$
;

CREATE OR REPLACE FUNCTION "uspDeleteReport"(report_id int)
RETURNS int
LANGUAGE 'plpgsql'
AS $$
DECLARE
	deleted_id INT;
BEGIN
	DELETE FROM public.reports as r
	WHERE r.id = report_id
	RETURNING r.id into deleted_id; 
	RETURN deleted_id;
END;$$
;


-- Sort Matches by Chronological Order
CREATE OR REPLACE FUNCTION "uspSeeMatchesChronological"(user_id int) 
RETURNS TABLE(int_1_id int,int_2_id int,int_1_type interaction_type,int_2_type interaction_type,
			  user_1_id int, user_2_id int, created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT i1.id AS "interaction_1_id",i2.id AS "interaction_2_id",i1.type AS "interaction_1_type", 
	i2.type AS "interaction_2_type",i1.source_user_id AS "user_1_id", i1.target_user_id AS "user_2_id",
	(SELECT Max(v)FROM (VALUES (i2.created_at), (i1.created_at) ) AS value(v)) AS "created_at"
	FROM interactions as i1,interactions as i2
	WHERE i1.source_user_id=i2.target_user_id AND i1.target_user_id=i2.source_user_id 
	AND i1.target_user_id=user_id AND ((i1.type='like' or i1.type='super_like') AND (i2.type='like' or i2.type='super_like'))
	ORDER BY created_at DESC;
END;$$
;



-- Testing Interaction Procedures
SELECT * FROM "uspSeeMatchesChronological"(2);


DROP FUNCTION IF EXISTS checkUserPremiumMirrorInteraction;
CREATE OR REPLACE FUNCTION checkUserPremiumMirrorInteraction(source_id int, target_id int)
RETURNS TABLE(source_user_id int, target_user_id int , is_source_premium boolean,is_target_premium boolean, "type" interaction_type)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT i.source_user_id,i.target_user_id,u.is_premium AS is_source_premium, u2.is_premium AS is_target_premium, i.type from public.interactions i
	INNER JOIN "users" u ON i.source_user_id = u.id
	INNER JOIN "users" u2 ON i.target_user_id = u2.id
	WHERE i.target_user_id = source_id AND i.source_user_id = target_id
	AND (i.type='like' or i.type='super_like')
	AND u.is_premium = true;
END;$$
