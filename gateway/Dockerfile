FROM amazoncorretto:11
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Dserver.port=8080","-jar","/app.jar"]