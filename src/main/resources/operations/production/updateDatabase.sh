#!/usr/bin/env bash

source config.shlib; # load the config library functions

echo 'Password per il server'
scp -P $(config_get EXTERNAL_SSH_PORT) $(config_get DB_INITIAL_FILENAME_COMPRESSED) root@$(config_get IP_ADDRESS):/root/

echo 'Password dell'\''utente '$(config_get DB_USER_NAME)''
ssh $(config_get DB_USER_NAME)@$(config_get IP_ADDRESS) -p $(config_get EXTERNAL_SSH_PORT) '\
psql postgres -c "ALTER DATABASE '$(config_get DB_NAME)' CONNECTION LIMIT 1"; \
psql postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '\'''$(config_get DB_NAME)''\''"; \
psql postgres -c "DROP DATABASE '$(config_get DB_NAME)'"'

echo 'Password per il server'
ssh root@$(config_get IP_ADDRESS) -p $(config_get EXTERNAL_SSH_PORT) '\
createdb --owner='$(config_get DB_USER_NAME)' --encoding=UTF8 '$(config_get DB_NAME)'; \
gzip --uncompress '$(config_get DB_INITIAL_FILENAME_COMPRESSED)'; \
pg_restore -d '$(config_get DB_NAME)' '$(config_get DB_INITIAL_FILENAME)'; \
rm '$(config_get DB_INITIAL_FILENAME)'; \
\
sh '$(config_get AS_BIN)'/asadmin restart-domain '$(config_get AS_DOMAIN_NAME)''
