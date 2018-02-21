#!/bin/bash

readonly APP_ID=1
readonly APP_NAME=censistant
readonly DB_IP_ADDRESS=192.168.2.251
readonly DB_EXTERNAL_SSH_PORT=22
readonly DB_NAME=${APP_NAME}${APP_ID}
readonly DB_USER_NAME=${APP_NAME}${APP_ID}
readonly DB_INITIAL_FILENAME=initial
readonly DB_INITIAL_FILENAME_COMPRESSED=${DB_INITIAL_FILENAME}.gz

echo 'Password per database server'
scp -P ${DB_EXTERNAL_SSH_PORT} ${DB_INITIAL_FILENAME_COMPRESSED} root@${DB_IP_ADDRESS}:/root/

echo 'Password dell'\''utente '${DB_USER_NAME}''
ssh ${DB_USER_NAME}@${DB_IP_ADDRESS} -p ${DB_EXTERNAL_SSH_PORT} '\
psql postgres -c "ALTER DATABASE '${DB_NAME}' CONNECTION LIMIT 1"; \
psql postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '\'''${DB_NAME}''\''"; \
psql postgres -c "DROP DATABASE '${DB_NAME}'"'

echo 'Password per database server'
ssh root@${DB_IP_ADDRESS} -p ${DB_EXTERNAL_SSH_PORT} '\
createdb --owner='${DB_USER_NAME}' --encoding=UTF8 '${DB_NAME}'; \
gzip --uncompress '${DB_INITIAL_FILENAME_COMPRESSED}'; \
pg_restore -d '${DB_NAME}' '${DB_INITIAL_FILENAME}'; \
rm '${DB_INITIAL_FILENAME}''
