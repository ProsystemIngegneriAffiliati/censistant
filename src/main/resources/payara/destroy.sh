#!/bin/sh

rm -r $HOME/censistant/
./asadmin delete-auth-realm censistantRealm
./asadmin delete-jdbc-resource jdbc/postgres_censistant
./asadmin delete-jdbc-connection-pool postgres_censistant_pool
rm ../glassfish/domains/domain1/lib/ext/postgresql-42.2.0.jar
