#!/bin/bash

readonly APP_ID=1
readonly APP_NAME=censistant
readonly EMAIL="mainardi@prosystemingegneri.com"

readonly DATABASE=${APP_NAME}${APP_ID}
readonly DATE=$(date +"%Y-%m-%d")
readonly FILE="/tmp/pg_dump_${DATABASE}_${DATE}.sql.gz"

pg_dump ${DATABASE} | gzip > ${FILE}
mutt -s "Backup ${DATABASE} ${DATE}" -a ${FILE} -- ${EMAIL} < /dev/null