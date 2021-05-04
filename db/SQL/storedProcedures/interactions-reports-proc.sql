-- ---- INTERACTIONS PROCEDURES
DROP TYPE IF Exists interactionData; 
CREATE TYPE interactionData AS(
	id int,
	source_user_id int,
	target_user_id int,
	type interaction_type,
	created_at timestamp	
);

CREATE OR REPLACE FUNCTION "uspReadInteraction"(interaction_id int) 
RETURNS TABLE(id int,source_user_id int,target_user_id int,type interaction_type,created_at timestamp)
LANGUAGE 'plpgsql'
AS $$
BEGIN
	RETURN QUERY
	SELECT * from public.interactions
	WHERE public.interactions.id = uspReadInteraction.interaction_id;
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
	WHERE public.interactions.source_user_id=uspReadUserSourceInteractions.source_id
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
	WHERE public.interactions.target_user_id=uspReadUserTargetInteractions.target_id
	LIMIT "limit" OFFSET page*"limit";
END;$$
;

CREATE OR REPLACE FUNCTION "uspUpdateInteraction"(
	id integer,
	source_user_id int DEFAULT NULL,
	target_user_id int DEFAULT NULL,
	type interaction_type DEFAULT NULL)
	RETURNS interactionData
    LANGUAGE 'plpgsql'
AS $$
DECLARE updated_interaction interactionData;
BEGIN
    UPDATE interactions
       SET source_user_id      = COALESCE(uspUpdateInteraction.source_user_id, interactions.source_user_id),
           target_user_id      = COALESCE(uspUpdateInteraction.target_user_id, interactions.target_user_id),
           "type"              = COALESCE(uspUpdateInteraction.type, interactions.type)
     WHERE interactions.id = uspUpdateInteraction.id
     RETURNING * INTO updated_interaction;
	 RETURN updated_interaction;
END;$$
;

CREATE OR REPLACE FUNCTION "uspDeleteInteraction"(interaction_id int)
RETURNS int
LANGUAGE 'plpgsql'
AS $$
BEGIN
	DELETE FROM public.interactions 
	WHERE public.interactions.id = interaction_id;
	RETURN interaction_id;
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
	id integer,
	reason varchar(400) DEFAULT NULL)
	RETURNS reportData
    LANGUAGE 'plpgsql'
AS $$
DECLARE updated_report reportData;
BEGIN
    UPDATE reports
       SET reason = COALESCE(uspUpdateReport.reason, reports.reason)
     WHERE reports.id = uspUpdateReport.id
	 RETURNING * INTO updated_report;
     RETURN updated_report;
END;$$
;

CREATE OR REPLACE FUNCTION "uspDeleteReport"(report_id int)
RETURNS int
LANGUAGE 'plpgsql'
AS $$
BEGIN
	DELETE FROM public.reports 
	WHERE public.reports.id = report_id;
	RETURN report_id;
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
	AND i1.target_user_id=user_id
	ORDER BY created_at DESC;
END;$$
;



-- Testing Interaction Procedures
SELECT * FROM "uspSeeMatchesChronological"(2)
