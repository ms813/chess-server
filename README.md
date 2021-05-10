# chess-server

A Spring Boot + Angular app that allows chess games to be played via a RESTful API, storing the games in an in-memory h2 database

## Back End

Back end runs on OpenJDK 15.0.1. Created using [Spring Initializr](https://start.spring.io/)


To build with maven, and run the unit and integration tests:
```mvn clean install```

To run the back end, either use your favourite IDE, or run:
```
java -jar target/chess-0.0.1-SNAPSHOT.jar
```
For API documentation, please check the swagger ui at http://localhost:8080/swagger-ui.html

## Front End

Front end runs on node 14.15.1 and npm 6.14.8. Created using Angular CLI 12.2.12

To run the front end:
```
cd web/chess
npm i
npm start
```
then open your browser at http://localhost:4200

To access the in-memory h2 console, go to http://localhost:8080/h2-console and log in with the following credentials:
* JDBC URL = `jdbc:h2:mem:testdb`
* User Name = `sa`
* Password field should be blank

Thanks and full credit to the awesome libraries [chesslib](https://github.com/bhlangonijr/chesslib) and [chess.ts](https://github.com/lubert/chess.ts) for all of the actual chess validation
