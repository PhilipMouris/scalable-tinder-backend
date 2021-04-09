-- Users

INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('ariannagrande@gmail.com', 123456789, false, false, NULL, 'Arianna', 'Grande');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('doja.cat@gmail.com', 123456789, false, false, NULL, 'Doja', 'Cat');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('billie.eilish@gmail.com', 123456789, false, false, NULL, 'Billie', 'Eilish');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('megan.stalyon@gmail.com', 123456789, false, false, NULL, 'Megan', 'The Stalyon');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('selena.gomez@gmail.com', 123456789, false, false, NULL, 'Selena', 'Gomez');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('justin.bieber@gmail.com', 123456789, false, false, NULL, 'Justin', 'Bieber');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('post.malone@gmail.com', 123456789, false, false, NULL, 'Post', 'Malone');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('taylor.swift@gmail.com', 123456789, false, true, 5186975364203198, 'Taylor', 'Swift');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('dua.lippa@gmail.com', 123456789, false, false, NULL, 'Dua', 'Lippa');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('jennifer.lopez@gmail.com', 123456789, false, false, NULL, 'Jennifer', 'Lopez');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('mariah.carey@gmail.com', 123456789, false, true, 5192086875353209, 'Mariah', 'Carey');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('drake@gmail.com', 123456789, false, true, 5186425319208694, 'Aubrey', 'Drake');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('dj.khaled@gmail.com', 123456789, false, false, NULL, 'DJ', 'Khaled');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('travis.scott@gmail.com', 123456789, false, false, NULL, 'Travis', 'Scott');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('jack.harlow@gmail.com', 123456789, false, false, NULL, 'Jack', 'Harlow');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('kanye.west@gmail.com', 123456789, false, true, 5192086975364205, 'Kanye', 'West');

	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('donald.trump@gmail.com', 123456789, false, false, NULL, 'Donald', 'Trump');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('liam.hemsworth@gmail.com', 123456789, false, false, NULL, 'Liam', 'Hemsworth');
	
	INSERT INTO public.users(
	email, password, is_banned, is_premium, credit_card_token, first_name, last_name)
	VALUES ('miley.cyrus@gmail.com', 123456789, false, false, NULL, 'Miley', 'Cyrus');
	
----------------------------------------------------------------------------------------------------

--Moderators

INSERT INTO public.moderators(
	email, password)
	VALUES ( 'hussein.badr@gmail.com', 123456789);
INSERT INTO public.moderators(
	email, password)
	VALUES ( 'youssef.sameh@gmail.com', 123456789);

----------------------------------------------------------------------------------------------------

--Interests

INSERT INTO public.interests(
	 name)
	VALUES ( 'Surfing');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Volunteering');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Tea');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Politics');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Art');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Instagram');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Spirituality');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Dog Lover');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'DIY');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Sports');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Cycling');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Foodie');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Astrology');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Netflix');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Photography');
	
	INSERT INTO public.interests(
	 name)
	VALUES ( 'Reading');	
	
	----------------------------------------------------------------------------------------------------
	
	--Interactions
	
	INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 16, 1, 'like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 16, 12, 'dislike');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 16, 13, 'super_like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 16, 14, 'dislike');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 16, 15, 'like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 1, 16, 'like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 1, 8, 'dislike');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 8, 12, 'dislike');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 11, 15, 'super_like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 15, 11, 'super_like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 12, 16, 'like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 18, 19, 'like');
	
INSERT INTO public.interactions(
	source_user_id, target_user_id, type)
	VALUES ( 19, 18, 'like');
	
----------------------------------------------------------------------------------------------------

--Reports

INSERT INTO public.reports(
	source_user_id, target_user_id, reason, created_at)
	VALUES (1, 17, 'racist bio', '2021-04-08 13:48:27.110024');
	

----------------------------------------------------------------------------------------------------

--Blocks

INSERT INTO public.blocks(
	source_user_id, target_user_id)
	VALUES (19, 18);
	
----------------------------------------------------------------------------------------------------

--Bans

INSERT INTO public.bans(
	moderator_id, user_id, reason, expiry_date)
	VALUES (1, 17, 'racist bio', '2021-09-09 13:48:27.110024');
	
----------------------------------------------------------------------------------------------------

--Transactions

INSERT INTO public.transactions(
	user_id, amount, created_at)
	VALUES ( 16, 19.99, '2021-04-09 13:48:27.110024');
	
INSERT INTO public.transactions(
	user_id, amount, created_at)
	VALUES ( 15, 39.98, '2021-04-07 13:48:27.110024');
	
INSERT INTO public.transactions(
	user_id, amount, created_at)
	VALUES ( 11, 59.97, '2021-04-01 13:48:27.110024');
	
INSERT INTO public.transactions(
	user_id, amount, created_at)
	VALUES ( 12, 119.94, '2021-03-09 13:48:27.110024');
	

	