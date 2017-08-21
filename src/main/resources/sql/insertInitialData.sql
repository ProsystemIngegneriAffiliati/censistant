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
