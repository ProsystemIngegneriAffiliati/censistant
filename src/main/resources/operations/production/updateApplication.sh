#!/usr/bin/env bash

source config.shlib; # load the config library functions

scp -P $(config_get EXTERNAL_SSH_PORT) ~/NetBeansProjects/$(config_get APP_NAME)/target/$(config_get APP_NAME).war root@$(config_get IP_ADDRESS):/root/
ssh root@$(config_get IP_ADDRESS) -p $(config_get EXTERNAL_SSH_PORT) '\
sh '$(config_get AS_BIN)'/asadmin undeploy '$(config_get APP_NAME)'; \
\
rm -r '$(config_get AS_ROOT_DIR)'/glassfish/domains/'$(config_get AS_DOMAIN_NAME)'/generated/*
\
sh '$(config_get AS_BIN)'/asadmin deploy --contextroot "/" ${HOME}/'$(config_get APP_NAME)'.war; \
\
sh '$(config_get AS_BIN)'/asadmin restart-domain '$(config_get AS_DOMAIN_NAME)''
