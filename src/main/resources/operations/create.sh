#!/bin/sh
readonly IDE_WORKSPACE=$HOME/NetBeansProjects
readonly AS_LIBFOLDER=$HOME/payara41/glassfish/domains/domain1/lib
readonly APP_NAME=censistant
readonly IP_ADDRESS=192.168.1.4
readonly POSTGRESQL_JDBC_DRIVER_VERSION=42.2.6
readonly DB_NAME="${APP_NAME}"
readonly DB_USER_NAME="${APP_NAME}"
readonly DB_USER_PASSWORD=PNpKjM19byxa6JBIKxFU
readonly AS_MAILPASSWORD_ALIAS_NAME="${APP_NAME}"-mailuser-alias
readonly PASSWORD_ALIAS_DIR=$HOME/NetBeansProjects/"${APP_NAME}"/src/main/resources/operations

mkdir $HOME/"${APP_NAME}"
mkdir $HOME/"${APP_NAME}"/documents
\
mvn -f "${IDE_WORKSPACE}"/"${APP_NAME}"/pom.xml -DincludeScope=provided -DexcludeArtifactIds=javax.mail,javaee-api,activation -DoutputDirectory="${AS_LIBFOLDER}"/ dependency:copy-dependencies
\
mvn dependency:copy -Dartifact=org.postgresql:postgresql:"${POSTGRESQL_JDBC_DRIVER_VERSION}" -DoutputDirectory="${AS_LIBFOLDER}"/ext/
\
./asadmin start-domain
./asadmin create-jdbc-connection-pool \
--datasourceclassname=org.postgresql.ds.PGSimpleDataSource \
--restype=javax.sql.DataSource \
--validationmethod=auto-commit \
--allownoncomponentcallers=false \
--nontransactionalconnections=false \
--driverclassname=org.postgresql.Driver \
--property user="${DB_USER_NAME}":\
password="${DB_USER_PASSWORD}":\
databaseName="${DB_NAME}":\
serverName="${IP_ADDRESS}":\
portNumber=5432:\
url=jdbc\\:postgresql\\://"${IP_ADDRESS}"\\:5432/"${DB_NAME}" \
postgres_"${APP_NAME}"_pool
./asadmin create-jdbc-resource --connectionpoolid postgres_"${APP_NAME}"_pool jdbc/postgres_"${APP_NAME}"
./asadmin create-auth-realm \
--classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm \
--property \
jaas-context=jdbcRealm:\
datasource-jndi=jdbc/postgres_"${APP_NAME}":\
user-table=user_app:\
user-name-column=user_name:\
password-column=pass_word:\
group-table=users_groups_app:\
group-name-column=groups_group_name:\
digest-algorithm=SHA-256:\
digestrealm-password-enc-algorithm=AES:\
encoding=base64:\
charset=UTF-8 \
"${APP_NAME}"Realm
\
./asadmin --passwordfile $PASSWORD_ALIAS_DIR/mailPassword create-password-alias $AS_MAILPASSWORD_ALIAS_NAME
\
./asadmin create-javamail-resource \
--mailhost smtps.aruba.it \
--mailuser gestionale@antifurto.com \
--fromaddress gestionale@antifurto.com \
--password \$\{ALIAS=$AS_MAILPASSWORD_ALIAS_NAME\} \
--auth true \
--property mail.smtp.ssl.enable=true:\
mail.smtp.ssl.trust=smtps.aruba.it:\
mail.smtp.host=smtps.aruba.it \
--debug true \
mail/"${APP_NAME}"Mail
\
./asadmin stop-domain
