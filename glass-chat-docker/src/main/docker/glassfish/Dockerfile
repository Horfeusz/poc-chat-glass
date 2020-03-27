FROM java:7
ENV GLASSFISH_HOME /opt/glassfish3
ENV PATH ${GLASSFISH_HOME}/bin:$PATH

RUN apt-get update && \
    apt-get install -y wget unzip pwgen expect && \
    wget http://download.oracle.com/glassfish/3.1.2.2/release/glassfish-3.1.2.2.zip && \
    unzip glassfish-3.1.2.2.zip -d /opt && \
    rm glassfish-3.1.2.2.zip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY ../../../password_1.txt /tmp
COPY ../../../password_2.txt /tmp
COPY postgresql-42.2.11.jre7.jar ${GLASSFISH_HOME}/glassfish/domains/domain1/lib

RUN asadmin --user admin --passwordfile /tmp/password_1.txt change-admin-password --domain_name domain1 ; asadmin start-domain domain1 ; asadmin --user admin --passwordfile /tmp/password_2.txt enable-secure-admin ; asadmin stop-domain domain1
RUN asadmin start-domain domain1 ; asadmin --user admin --passwordfile /tmp/password_2.txt create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property "user=postgres:databaseName=postgres:password=postgres:portNumber=5432:serverName=database" PocSecurityPool ; asadmin --user admin --passwordfile /tmp/password_2.txt create-jdbc-resource --connectionpoolid PocSecurityPool jdbc/PocSecurityDS ; asadmin stop-domain domain1

# 4848 (administration), 8080 (HTTP listener), 8181 (HTTPS listener)
EXPOSE 4848 8080 8181 3700

CMD ["asadmin", "start-domain", "-v"]
