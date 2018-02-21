#!/bin/sh

readonly IP_ADDRESS=192.168.2.170
readonly POSTGRESQL_JDBC_DRIVER=postgresql-42.2.1.jar

mkdir $HOME/censistant
mkdir $HOME/censistant/images
mkdir $HOME/censistant/documents
wget -P ../glassfish/domains/domain1/lib/ext/ https://jdbc.postgresql.org/download/$POSTGRESQL_JDBC_DRIVER
./asadmin start-domain
./asadmin create-jdbc-connection-pool \
--datasourceclassname=org.postgresql.ds.PGSimpleDataSource \
--restype=javax.sql.DataSource \
--validationmethod=auto-commit \
--allownoncomponentcallers=false \
--nontransactionalconnections=false \
--driverclassname=org.postgresql.Driver \
--property user=censistant:\
password=PNpKjM19byxa6JBIKxFU:\
databaseName=censistant:\
serverName=$IP_ADDRESS:\
portNumber=5432:\
url=jdbc\\:postgresql\\://$IP_ADDRESS\\:5432/censistant \
postgres_censistant_pool
./asadmin create-jdbc-resource --connectionpoolid postgres_censistant_pool jdbc/postgres_censistant
./asadmin create-auth-realm \
--classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm \
--property \
jaas-context=jdbcRealm:\
datasource-jndi=jdbc/postgres_censistant:\
user-table=user_app:\
user-name-column=user_name:\
password-column=pass_word:\
group-table=users_groups_app:\
group-name-column=groups_group_name:\
digest-algorithm=SHA-256:\
digestrealm-password-enc-algorithm=AES:\
encoding=base64:\
charset=UTF-8 \
censistantRealm
./asadmin stop-domain