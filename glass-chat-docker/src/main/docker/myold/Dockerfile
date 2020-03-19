FROM openjdk:7-jdk
ENV GLASSFISH_HOME /usr/local/glassfish
ENV PATH ${GLASSFISH_HOME}/bin:$PATH
ENV GLASSFISH_PKG latest-glassfish.zip
ENV GLASSFISH_URL https://download.oracle.com/glassfish/3.1/nightly/latest-glassfish.zip

RUN mkdir -p ${GLASSFISH_HOME}

WORKDIR ${GLASSFISH_HOME}

RUN set -x
RUN curl -fSL ${GLASSFISH_URL} -o ${GLASSFISH_PKG}
RUN unzip -o $GLASSFISH_PKG
RUN rm -f $GLASSFISH_PKG
RUN mv glassfish3/* ${GLASSFISH_HOME}
RUN rm -Rf glassfish3

RUN addgroup glassfish_grp
RUN adduser --system glassfish
RUN usermod -G glassfish_grp glassfish
RUN chown -R glassfish:glassfish_grp ${GLASSFISH_HOME}
RUN chmod -R 777 ${GLASSFISH_HOME}

COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

USER glassfish

ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 4848 8080 8181
CMD ["asadmin", "start-domain", "-v"]