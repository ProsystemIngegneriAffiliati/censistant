#!/bin/sh

readonly APP_ID=1
readonly APP_NAME=censistant
readonly DB_IP_ADDRESS=192.168.2.250
readonly DB_NAME=${APP_NAME}${APP_ID}
readonly DB_USER_NAME=${APP_NAME}${APP_ID}
readonly POSTGRESQL_JDBC_DRIVER=postgresql-42.2.0.jar
readonly AS_IP_ADDRESS=192.168.2.249
readonly AS_EXTERNAL_SSH_PORT=22
readonly AS_INSTALL_DIR=/opt
readonly AS_ROOT_DIR=${AS_INSTALL_DIR}/payara41
readonly AS_BIN=${AS_ROOT_DIR}/bin
readonly AS_VERSION=4.1.2.174
readonly AS_DOMAIN_NAME=payaradomain
readonly AS_PASSWORD_ALIAS_NAME=${APP_NAME}-dbuser-alias

echo 'Password per application server'
scp -P ${AS_EXTERNAL_SSH_PORT} ~/NetBeansProjects/${APP_NAME}/target/${APP_NAME}.war root@${AS_IP_ADDRESS}:/root/

echo 'Password per application server'
ssh root@${AS_IP_ADDRESS} -p ${AS_EXTERNAL_SSH_PORT} '\
sh '${AS_BIN}'/asadmin undeploy '${APP_NAME}'; \
\
rm -r '${AS_ROOT_DIR}'/glassfish/domains/'${AS_DOMAIN_NAME}'/generated/*
\
sh '${AS_BIN}'/asadmin deploy ${HOME}/'${APP_NAME}'.war'
