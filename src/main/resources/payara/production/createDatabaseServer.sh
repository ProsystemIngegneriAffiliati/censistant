#!/bin/bash

readonly APP_ID=1
readonly APP_NAME=censistant
readonly DB_IP_ADDRESS=192.168.2.250
readonly DB_EXTERNAL_SSH_PORT=22
readonly DB_NAME=${APP_NAME}${APP_ID}
readonly DB_USER_NAME=${APP_NAME}${APP_ID}
readonly DB_INITIAL_FILENAME=initial
readonly DB_INITIAL_FILENAME_COMPRESSED=${DB_INITIAL_FILENAME}.gz

echo 'Password per database server'
scp -P ${DB_EXTERNAL_SSH_PORT} ${DB_INITIAL_FILENAME_COMPRESSED} root@${DB_IP_ADDRESS}:/root/

echo 'Password per database server'
ssh root@${DB_IP_ADDRESS} -p ${DB_EXTERNAL_SSH_PORT} '\
createuser --createdb --pwprompt --echo '${DB_USER_NAME}'; \
createdb --owner='${DB_USER_NAME}' --encoding=UTF8 '${DB_NAME}'; \
gzip --uncompress '${DB_INITIAL_FILENAME_COMPRESSED}'; \
pg_restore -d '${DB_NAME}' '${DB_INITIAL_FILENAME}''
