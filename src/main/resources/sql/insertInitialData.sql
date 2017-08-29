INSERT INTO group_app(group_name) VALUES('user')
INSERT INTO group_app(group_name) VALUES('admin')

INSERT INTO user_app(user_name, name, pass_word, version) VALUES('maina', 'Mainardi Davide', 'PuYC2SlAA74yPJE0eporJcAYhJfayYF+eqstExAB7ws=', 0)
INSERT INTO user_app(user_name, name, pass_word, version) VALUES('guest', 'Guest User', 'hJg8YPfarcHLhphiH4AsDZ+aPDwpXIEHSPsEgRXBhuw=', 0)
INSERT INTO user_app(user_name, name, pass_word, version) VALUES('demoAdmin', 'Demo Admin', 'KpdRbDVLaISM29j1SiJqClWyHtE44getbFy7nACqWuo=', 0)

INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('maina', 'user')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('maina', 'admin')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('guest', 'user')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('demoAdmin', 'user')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('demoAdmin', 'admin')

INSERT INTO provenance(name, version) VALUES('Altro', 0)
INSERT INTO provenance(name, version) VALUES('CISL', 0)
INSERT INTO provenance(name, version) VALUES('Internet', 0)
INSERT INTO provenance(name, version) VALUES('Nostro cliente diretto', 0)
INSERT INTO provenance(name, version) VALUES('Nostro cliente indiretto', 0)
INSERT INTO provenance(name, version) VALUES('Pagine gialle', 0)
INSERT INTO provenance(name, version) VALUES('Sito CSA', 0)
INSERT INTO provenance(name, version) VALUES('Sito pagine gialle', 0)
INSERT INTO provenance(name, version) VALUES('Stampa', 0)
INSERT INTO provenance(name, version) VALUES('Ufficio', 0)
INSERT INTO provenance(name, version) VALUES('Volantino', 0)

INSERT INTO customersupplier(businessname, name, iscustomer, issupplier, taxcode, vatregistrationnumber, notes, provenance_id, version) VALUES('Prosystem Ingegneri Affiliati di Gilli Enrico & C.', 'Prosystem', true, false, '10805010013', '10805010013', 'Cliente o fornitore di prova', 4, 0)
INSERT INTO plant(customersupplier_id, isheadoffice, name, address, phone, fax, email, version) VALUES(1, true, 'Sede Prosystem', 'Via Nazionale, 24 10064 Pinerolo (TO)', '0121 202147', '0121 305343', 'info@prosystemingegneri.com', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Gilli Enrico', null, '3291234567', 'gilli@prosystemingegneri.com', 'Titolare', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Mainardi Davide', null, '3461234567', 'mainardi@prosystemingegneri.com', null, 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Carioti Ilaria', null, '3931234567', 'carioti@prosystemingegneri.com', 'In maternità fino a gennaio 2018', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Elena', '0121202147', null, 'info@prosystemingegneri.com', 'Segretaria part-time al mattino', 0)
