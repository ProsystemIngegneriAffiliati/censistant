scp /home/maina/NetBeansProjects/censistant/target/censistant.war root@192.168.2.252:/root/

ssh root@192.168.2.252

cd /payara41/bin/
./asadmin undeploy censistant
rm -r $HOME/censistant/
./asadmin delete-auth-realm censistantRealm
./asadmin delete-jdbc-resource jdbc/postgres_censistant
./asadmin delete-jdbc-connection-pool postgres_censistant_pool
rm ../glassfish/domains/domain1/lib/ext/https://jdbc.postgresql.org/download/postgresql-42.1.4.jar
./asadmin stop-domain

cd /payara41/bin/
mkdir $HOME/censistant
mkdir $HOME/censistant/images
mkdir $HOME/censistant/documents
wget -P ../glassfish/domains/domain1/lib/ext/ https://jdbc.postgresql.org/download/postgresql-42.1.4.jar
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
serverName=127.0.0.1:\
portNumber=5432:\
url=jdbc\\:postgresql\\://127.0.0.1\\:5432/censistant \
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
./asadmin start-domain
./asadmin deploy /root/censistant.war

exit
