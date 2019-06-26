#!/bin/sh

readonly APP_NAME=censistant
readonly DB_NAME=$APP_NAME
readonly DB_USER_NAME=$APP_NAME

createuser -d -P $DB_USER_NAME
createdb --owner=$DB_USER_NAME --encoding=UTF8 $DB_NAME
