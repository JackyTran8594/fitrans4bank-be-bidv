
### STAGE 1: MAVEN BUILD ###
FROM maven:3.8.5-openjdk-11 AS builders
# create app directory in images and copies pom.xml into it
COPY ./ /app/
WORKDIR /app/
# run mvn
RUN mvn clean install


### STAGE 2: DEPLOY APPLICATION
FROM openjdk:11.0.14-jdk
WORKDIR /app
COPY --from=builders /app/target/fitrans4bank-be-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["java","-jar", "fitrans4bank-be-0.0.1-SNAPSHOT.jar"]


