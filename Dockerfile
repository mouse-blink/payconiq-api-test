FROM maven:3.8.6-openjdk-11
WORKDIR workspace
COPY . .
RUN mvn clean install  -DskipTests
EXPOSE 8000
CMD  mvn surefire-report:report && python3 -m http.server 8000