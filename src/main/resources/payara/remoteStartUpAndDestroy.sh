#!/bin/sh

readonly APP_ID=1
readonly APP_NAME=censistant
readonly DB_IP_ADDRESS=192.168.2.251
readonly DB_EXTERNAL_SSH_PORT=22
readonly DB_NAME=${APP_NAME}${APP_ID}
readonly DB_USER_NAME=${APP_NAME}${APP_ID}
readonly DB_USER_PASSWORD=PNpKjM19byxa6JBIKxFU
readonly POSTGRESQL_JDBC_DRIVER=postgresql-42.2.1.jar
readonly AS_IP_ADDRESS=192.168.2.252
readonly AS_EXTERNAL_SSH_PORT=22
readonly AS_ROOT_DIR=/payara41
readonly AS_BIN=${AS_ROOT_DIR}/bin

echo 'Password per application server'
ssh root@${AS_IP_ADDRESS} -p ${AS_EXTERNAL_SSH_PORT} '\
sh '${AS_BIN}'/asadmin undeploy '${APP_NAME}'; \
rm -r ${HOME}/'${APP_NAME}'/; \
sh '${AS_BIN}'/asadmin delete-auth-realm '${APP_NAME}'Realm; \
sh '${AS_BIN}'/asadmin delete-jdbc-resource jdbc/postgres_'${APP_NAME}'; \
sh '${AS_BIN}'/asadmin delete-jdbc-connection-pool postgres_'${APP_NAME}'_pool; \
rm '${AS_ROOT_DIR}'/glassfish/domains/domain1/lib/ext/'${POSTGRESQL_JDBC_DRIVER}'; \
rm -r '${AS_ROOT_DIR}'/glassfish/domains/domain1/generated/*; \
sh '${AS_BIN}'/asadmin stop-domain'

echo 'Password per database server'
ssh root@${DB_IP_ADDRESS} -p ${DB_EXTERNAL_SSH_PORT} '\
dropdb '${DB_NAME}'; \
createdb --owner='${DB_USER_NAME}' --encoding=UTF8 '${DB_NAME}''

echo 'Password per application server'
scp -P ${AS_EXTERNAL_SSH_PORT} ~/NetBeansProjects/${APP_NAME}/target/${APP_NAME}.war root@${AS_IP_ADDRESS}:/root/

echo 'Password per application server'
ssh root@${AS_IP_ADDRESS} -p ${AS_EXTERNAL_SSH_PORT} '\
mkdir ${HOME}/'${APP_NAME}'; \
mkdir ${HOME}/'${APP_NAME}'/images; \
mkdir ${HOME}/'${APP_NAME}'/documents; \
wget -P '${AS_ROOT_DIR}'/glassfish/domains/domain1/lib/ext/ https://jdbc.postgresql.org/download/'${POSTGRESQL_JDBC_DRIVER}'; \
sh '${AS_BIN}'/asadmin start-domain; \
sh '${AS_BIN}'/asadmin create-jdbc-connection-pool \
--datasourceclassname=org.postgresql.ds.PGSimpleDataSource \
--restype=javax.sql.DataSource \
--validationmethod=auto-commit \
--allownoncomponentcallers=false \
--nontransactionalconnections=false \
--driverclassname=org.postgresql.Driver \
--property user='${DB_USER_NAME}':\
password='${DB_USER_PASSWORD}':\
databaseName='${DB_NAME}':\
serverName='${DB_IP_ADDRESS}':\
portNumber=5432:\
url=jdbc\\:postgresql\\://'${DB_IP_ADDRESS}'\\:5432/'${DB_NAME}' \
postgres_'${APP_NAME}'_pool; \
sh '${AS_BIN}'/asadmin create-jdbc-resource --connectionpoolid postgres_'${APP_NAME}'_pool jdbc/postgres_'${APP_NAME}'; \
sh '${AS_BIN}'/asadmin create-auth-realm \
--classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm \
--property \
jaas-context=jdbcRealm:\
datasource-jndi=jdbc/postgres_'${APP_NAME}':\
user-table=user_app:\
user-name-column=user_name:\
password-column=pass_word:\
group-table=users_groups_app:\
group-name-column=groups_group_name:\
digest-algorithm=SHA-256:\
digestrealm-password-enc-algorithm=AES:\
encoding=base64:\
charset=UTF-8 \
'${APP_NAME}'Realm; \
sh '${AS_BIN}'/asadmin stop-domain; \
sh '${AS_BIN}'/asadmin start-domain; \
sh '${AS_BIN}'/asadmin deploy /root/'${APP_NAME}'.war'
