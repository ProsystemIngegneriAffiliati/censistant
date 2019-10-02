#!/bin/sh

readonly AS_LIBFOLDER=$HOME/payara41/glassfish/domains/domain1/lib
readonly APP_NAME=censistant

./asadmin delete-auth-realm "${APP_NAME}"Realm
./asadmin delete-jdbc-resource jdbc/postgres_"${APP_NAME}"
./asadmin delete-jdbc-connection-pool postgres_"${APP_NAME}"_pool
./asadmin delete-javamail-resource mail/"${APP_NAME}"Mail
rm -r $HOME/"${APP_NAME}"/
rm "${AS_LIBFOLDER}"/ext/*
rm "${AS_LIBFOLDER}"/*
