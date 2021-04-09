--## REPORT USER PROCEDURE ##--

DROP TYPE IF Exists reportData; 
CREATE TYPE reportData AS(
	id int,
	source_user_id int, 
	target_user_id int,
	reason varchar(400),
	created_At timestamp
	);

-- DROP FUNCTION IF EXISTS uspBlockUser;
CREATE or REPLACE FUNCTION uspReportUser(
	_source_user_id int ,
	_target_user_id int ,
	_reason varchar(400),
	_created_at timestamp
	)
	RETURNS reportData
	LANGUAGE plpgsql
AS $$
DECLARE
	new_report_data reportData;
BEGIN
	INSERT INTO public.blocks(source_user_id, target_user_id, reason, created_at) VALUES (_source_user_id, _target_user_id, _reason, _created_at)
	RETURNING * INTO new_report_data;
	RETURN new_report_data;
END;
$$;
