FROM java:8
MAINTAINER bofa bofa1exx@gmail.com
ADD chat-server-1.0-SNAPSHOT.jar app.jar
# time zone
RUN echo "Asia/Shanghai" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata
RUN sh -c 'touch /app.jar'
EXPOSE 8000
CMD ["java", "-version"]
ENTRYPOINT ["java", "-jar", "/app.jar"]