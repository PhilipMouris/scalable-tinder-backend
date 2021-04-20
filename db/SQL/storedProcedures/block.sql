--## BLOCK USER PROCEDURE ##--

--DROP TYPE IF Exists blockData; 
CREATE TYPE blockData AS(
	source_user_id int, 
	target_user_id int,
	created_At timestamp
	);

-- DROP FUNCTION IF EXISTS uspBlockUser;
CREATE or REPLACE FUNCTION uspBlockUser(
	_source_user_id int ,
	_target_user_id int ,
	_created_at timestamp
	)
	RETURNS blockData
	LANGUAGE plpgsql
AS $$
DECLARE
	new_block_data blockData;
BEGIN
	INSERT INTO public.blocks(source_user_id, target_user_id, created_at) VALUES (_source_user_id, _target_user_id, _created_at)
	RETURNING * INTO new_block_data;
	RETURN new_block_data;
END;
$$;

