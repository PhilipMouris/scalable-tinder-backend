CREATE TYPE "interactionData" As(
	id int,
	source_user_id int,
	target_user_id int,
	"type" interaction_type,
	created_at timestamp
);
	
CREATE or replace function uspCreateInteraction ( 
	source_user_id int, 
	target_user_id int, 
	_type interaction_type
)returns "interactionData"
language plpgsql
AS $$
	declare
		interaction_data "interactionData";	
	begin
		insert into public.interactions(source_user_id,target_user_id,"type") 
		values (source_user_id,target_user_id,_type)	
	returning * into interaction_data;
	return interaction_data;
	commit;
	end; $$