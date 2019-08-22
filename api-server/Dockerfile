FROM quay.io/enmasse/java-base:11-1

ARG version=latest
ENV VERSION ${version}
ADD target/api-server-${VERSION}.jar /api-server.jar

EXPOSE 8899/tcp
CMD ["/opt/run-java/launch_java.sh", "/api-server.jar"]
