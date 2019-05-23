FROM java:8
MAINTAINER bofa bofa1exx@gmail.com
ADD chat-server-1.0-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8001
# need dynamic expose the port, because server bind the port is not real which u choose.
CMD ["java", "-version"]
ENTRYPOINT ["java", "-jar", "/app.jar"]