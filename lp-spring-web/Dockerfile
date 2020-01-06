FROM adoptopenjdk/openjdk9-openj9:latest
ENV LANG="en_US.UTF-8"
ENV SYSFONT="latarcyrheb-sun16"
ADD /target/lp-spring-web.jar lp-spring-web.jar
ADD /target/lib lib
ADD /target/wqr-lib lib
EXPOSE 8020
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dloader.path=./lib", "-jar","/lp-spring-web.jar"]