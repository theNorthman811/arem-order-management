
insert into arem.seller (id,address,creation_date,email,first_name,last_name,modif_date,phone_number,pick_name,version,password,is_account_non_expired,is_account_non_locked,is_admin,is_credentials_non_expired,is_enabled,create_seller_id,modif_seller_id)
VALUES ('1', '53 Rue Ambroise Thomas 95100 Argenteuil', '2020-04-08 10:34:52', 'r.haouche@hotmail.com', 'ramdane', 'haouche', '2020-04-08 08:42:19', '0684099367', 'ramdane', '2', '$2a$10$c.v2ViisqB.G2s7wbq4MCu4mi2MYOe7gHZaGY7GXzN2G6oP9pouE.', true, true, true, true, true, 1, 1);


insert into arem.seller (id,address,creation_date,email,first_name,last_name,modif_date,phone_number,pick_name,version,password,is_account_non_expired,is_account_non_locked,is_admin,is_credentials_non_expired,is_enabled,create_seller_id,modif_seller_id)
VALUES ('2', 'Berkouka Maatkas ALGERIE', '2020-04-08 08:50:40', 'samir.khelfane@hotmail.com', 'SAMIR', 'KHELFANE', '2020-04-08 13:32:40', '00213568745', 'commerçant', '2', '$2a$10$DuIu702AyhWJfcaGlCTXjObdQklqdmgrZG4.5Rz43vhZHCXvVie2i', true, true, false, true, true, '1', '1');



--  Compte ADMIN (mot de passe = "admin")
INSERT INTO arem.seller (
  id, address, creation_date, email, first_name, last_name, modif_date,
  phone_number, pick_name, version, password,
  is_account_non_expired, is_account_non_locked, is_admin,
  is_credentials_non_expired, is_enabled,
  create_seller_id, modif_seller_id
) VALUES (
  3, '12 rue du test', NOW(), 'katia@gmail.com', 'Katia', 'Admin', NOW(),
  '0600000000', 'katia-admin', '1', 
  '$2a$10$bqxG.hbD3R6WqRbV.d.htOBCf1prLsshShw3PHpj0YGnAwnFK.QK.', -- "admin"
  true, true, true, true, true,
  1, 1
);

-- Compte VENDEUSE (mot de passe = "vendeuse")
INSERT INTO arem.seller (
  id, address, creation_date, email, first_name, last_name, modif_date,
  phone_number, pick_name, version, password,
  is_account_non_expired, is_account_non_locked, is_admin,
  is_credentials_non_expired, is_enabled,
  create_seller_id, modif_seller_id
) VALUES (
  4, '34 avenue du test', NOW(), 'vendeuse@gmail.com', 'Katia', 'Vendeuse', NOW(),
  '0600000001', 'katia-vendeuse', '1', 
  '$2a$10$JKI3JqKUyqwBoFUDmA04YedLPW5mt8CJjfbYXxKjAx7lzGu7W7kGm', -- "vendeuse"
  true, true, false, true, true,
  1, 1
);

COMMIT;

commit;