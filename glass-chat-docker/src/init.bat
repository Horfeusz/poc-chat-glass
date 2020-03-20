asadmin --user admin --passwordfile c:/password_1.txt change-admin-password --domain_name domain1
asadmin start-domain domain1
asadmin --user admin --passwordfile c:/password_2.txt enable-secure-admin
asadmin stop-domain domain1

asadmin start-domain domain1
asadmin --user admin --passwordfile c:/password_2.txt create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property "user=postgres:databaseName=postgres:password=postgres:portNumber=5432:serverName=localhost" PocSecurityPool
asadmin --user admin --passwordfile c:/password_2.txt create-jdbc-resource --connectionpoolid PocSecurityPool jdbc/PocSecurityDS
asadmin --user admin --passwordfile c:/password_2.txt set server.security-service.property.default-digest-algorithm=none



asadmin stop-domain domain1
