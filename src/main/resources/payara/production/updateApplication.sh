#!/bin/sh

readonly APP_ID=
readonly APP_NAME=censistant
readonly AS_IP_ADDRESS=192.168.2.252
readonly AS_EXTERNAL_SSH_PORT=22
readonly AS_INSTALL_DIR=/opt
readonly AS_ROOT_DIR=${AS_INSTALL_DIR}/payara41
readonly AS_BIN=${AS_ROOT_DIR}/bin
readonly AS_DOMAIN_NAME=payaradomain

echo 'Password per application server'
scp -P ${AS_EXTERNAL_SSH_PORT} ~/NetBeansProjects/${APP_NAME}/target/${APP_NAME}.war root@${AS_IP_ADDRESS}:/root/

echo 'Password per application server'
ssh root@${AS_IP_ADDRESS} -p ${AS_EXTERNAL_SSH_PORT} '\
sh '${AS_BIN}'/asadmin undeploy '${APP_NAME}'; \
\
rm -r '${AS_ROOT_DIR}'/glassfish/domains/'${AS_DOMAIN_NAME}'/generated/*
\
sh '${AS_BIN}'/asadmin deploy ${HOME}/'${APP_NAME}'.war; \
sh '${AS_BIN}'/asadmin restart-domain '${AS_DOMAIN_NAME}''
