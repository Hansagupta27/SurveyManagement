##Build and package
FROM maven:3.8.6-amazoncorretto-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

##run jar
FROM openjdk:11
COPY --from=build /usr/src/app/target/SurveyManagement-0.0.1-SNAPSHOT.jar /usr/app/SurveyManagement-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/SurveyManagement-0.0.1-SNAPSHOT.jar"]