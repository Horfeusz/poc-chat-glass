# Application POC-CHAT 
GlassFish and WildFly remote EJB integration.

## Introduction
Here is an example of how to integrate EJB servers: GlassFish 3.1 (Java 1.7) and WildFly 17 (Java 1.8)
Integration is at the EJB level. This is configuration and application for GlassFish.\
Source application for [Wildfy]


## Configuration for GlassFish 3.1.2.1

### Security
The Application will be use JDBC-realm. In the sources is located docker image.
This image create database PostgreSQL. Script [init.sql] create examples structure and realms.\
The image can create the command:
```sh
docker build -t poc-image-postgres .
```

### Copy JDBC driver to Server: 
Copy file [postgresql-42.2.11.jre7.jar] to ${GLASSFISH_HOME}/glassfish/domains/domain1/lib

### Run commands from file init.bat.
Full Script is in glass-chat-docker/src folder.
Customize your own localisation file password_1.txt, password_2.txt

##### Set password for admin
```sh
asadmin --user admin --passwordfile password_1.txt change-admin-password --domain_name domain1
```
#### Enable secure admin
Start domain first.
```sh
asadmin --user admin --passwordfile password_2.txt enable-secure-admin
```
#### Create JDBC connection pool
Start domain first.
```sh
asadmin --user admin --passwordfile password_2.txt create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property "user=postgres:databaseName=postgres:password=postgres:portNumber=5432:serverName=localhost" PocSecurityPool
```
#### Create JDBC resource
Start domain first.
```sh
asadmin --user admin --passwordfile password_2.txt create-jdbc-resource --connectionpoolid PocSecurityPool jdbc/PocSecurityDS
```
#### Set default digest algorithm on "none"
Start domain first.
```sh
asadmin --user admin --passwordfile password_2.txt set server.security-service.property.default-digest-algorithm=none
```
#### Create authentication realm for remote EJB
This realm will be used for remote EJB communication \
Start domain first.
```sh
asadmin --user admin --passwordfile password_2.txt create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property "jaas-context=jdbcRealm:datasource-jndi=jdbc/PocSecurityDS:user-table=poc_user:user-name-column=login:password-column=hash_password:group-table=vrole_use:group-name-column=name:group-table-user-name-column=login" PocRealm
```
#### Create authentication realm for web
This realm will be used for REST and WebServices \
Start domain first.
```sh
asadmin --user admin --passwordfile c:/password_2.txt create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property "jaas-context=jdbcRealm:datasource-jndi=jdbc/PocSecurityDS:digest-algorithm=SHA-256:user-table=poc_user:user-name-column=login:password-column=hash_password:group-table=vrole_use:group-name-column=name:group-table-user-name-column=login" PocHashRealm
```
#### Create system properties that point to the host and port of the WildFly server
Start domain first.
```sh
asadmin --user admin --passwordfile password_2.txt create-system-properties --target server-config poc-chat-wildfly-host=127.0.0.1
asadmin --user admin --passwordfile password_2.txt create-system-properties --target server-config poc-chat-wildfly-port=8090
```

[init.sql]: https://github.com/Horfeusz/poc-chat-glass/blob/master/glass-chat-docker/src/main/docker/postgres/init.sql
[Wildfy]: https://github.com/Horfeusz/poc-chat
[postgresql-42.2.11.jre7.jar]: https://jdbc.postgresql.org/download/postgresql-42.2.11.jre7.jar 
