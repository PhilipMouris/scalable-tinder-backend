CREATE or replace procedure uspCreateInteraction ( 
	source_user_id int, 
	target_user_id int, 
	"type" interaction_type
)
language plpgsql
	
AS $$
	begin

		insert into public.interactions(source_user_id,target_user_id,"type",created_at) 
		values (source_user_id,target_user_id,"type",CURRENT_TIMESTAMP);		
	
	commit;
	end; $$