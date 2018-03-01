#!/bin/sh

readonly APP_ID=
readonly APP_NAME=censistant
readonly DB_IP_ADDRESS=192.168.2.251
readonly DB_NAME=${APP_NAME}${APP_ID}
readonly DB_USER_NAME=${APP_NAME}${APP_ID}
readonly POSTGRESQL_JDBC_DRIVER=postgresql-42.2.1.jar
readonly AS_IP_ADDRESS=192.168.2.252
readonly AS_EXTERNAL_SSH_PORT=22
readonly AS_INSTALL_DIR=/opt
readonly AS_ROOT_DIR=${AS_INSTALL_DIR}/payara41
readonly AS_BIN=${AS_ROOT_DIR}/bin
readonly AS_VERSION=4.1.2.181
readonly AS_DOMAIN_NAME=payaradomain
readonly AS_PASSWORD_ALIAS_NAME=${APP_NAME}-dbuser-alias

echo 'Password per application server'
scp -P ${AS_EXTERNAL_SSH_PORT} dbUserPassword root@${AS_IP_ADDRESS}:/root/

echo 'Password per application server'
ssh root@${AS_IP_ADDRESS} -p ${AS_EXTERNAL_SSH_PORT} '\
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list; \
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list; \
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886; \
apt-get update; \
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections; \
apt-get -y install oracle-java8-installer; \
\
cd '${AS_INSTALL_DIR}'; \
wget https://s3-eu-west-1.amazonaws.com/payara.fish/Payara+Downloads/Payara+'${AS_VERSION}'/payara-'${AS_VERSION}'.zip; \
unzip payara-'${AS_VERSION}'.zip; \
rm payara-'${AS_VERSION}'.zip; \
\
wget -P '${AS_ROOT_DIR}'/glassfish/domains/'${AS_DOMAIN_NAME}'/lib/ext/ https://jdbc.postgresql.org/download/'${POSTGRESQL_JDBC_DRIVER}'; \
\
mkdir ${HOME}/'${APP_NAME}'; \
mkdir ${HOME}/'${APP_NAME}'/images; \
mkdir ${HOME}/'${APP_NAME}'/documents; \
\
sh '${AS_BIN}'/asadmin start-domain '${AS_DOMAIN_NAME}'; \
\
sh '${AS_BIN}'/asadmin --passwordfile ${HOME}/dbUserPassword create-password-alias '${AS_PASSWORD_ALIAS_NAME}'; \
\
sh '${AS_BIN}'/asadmin create-jdbc-connection-pool \
--datasourceclassname=org.postgresql.ds.PGSimpleDataSource \
--restype=javax.sql.DataSource \
--validationmethod=auto-commit \
--allownoncomponentcallers=false \
--nontransactionalconnections=false \
--driverclassname=org.postgresql.Driver \
--property user='${DB_USER_NAME}':\
password=\$\{ALIAS='${AS_PASSWORD_ALIAS_NAME}'\}:\
databaseName='${DB_NAME}':\
serverName='${DB_IP_ADDRESS}':\
portNumber=5432:\
url=jdbc\\:postgresql\\://'${DB_IP_ADDRESS}'\\:5432/'${DB_NAME}' \
postgres_'${APP_NAME}'_pool; \
\
sh '${AS_BIN}'/asadmin create-jdbc-resource --connectionpoolid postgres_'${APP_NAME}'_pool jdbc/postgres_'${APP_NAME}'; \
\
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
\
rm ${HOME}/dbUserPassword
\
sh '${AS_BIN}'/asadmin restart-domain '${AS_DOMAIN_NAME}''
