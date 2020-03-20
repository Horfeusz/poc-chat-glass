asadmin --user admin --passwordfile c:/password_1.txt change-admin-password --domain_name domain1

asadmin start-domain domain1
asadmin --user admin --passwordfile c:/password_2.txt enable-secure-admin
asadmin stop-domain domain1

asadmin start-domain domain1
asadmin --user admin --passwordfile c:/password_2.txt create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property "user=postgres:databaseName=postgres:password=postgres:portNumber=5432:serverName=localhost" PocSecurityPool
asadmin --user admin --passwordfile c:/password_2.txt create-jdbc-resource --connectionpoolid PocSecurityPool jdbc/PocSecurityDS
asadmin --user admin --passwordfile c:/password_2.txt set server.security-service.property.default-digest-algorithm=none
asadmin --user admin --passwordfile c:/password_2.txt create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property "jaas-context=jdbcRealm:datasource-jndi=jdbc/PocSecurityDS:user-table=poc_user:user-name-column=login:password-column=password:group-table=vrole_use:group-name-column=name:group-table-user-name-column=login" PocRealm
asadmin --user admin --passwordfile c:/password_2.txt create-system-properties --target server-config poc-chat-wildfly-host=127.0.0.1
asadmin --user admin --passwordfile c:/password_2.txt create-system-properties --target server-config poc-chat-wildfly-port=8090

asadmin stop-domain domain1
