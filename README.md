# chess-server

A Spring Boot server that allows chess games to be played via a RESTful API, storing the games in an in-memory h2 database

Runs on OpenJDK 15.0.1. Created using [Spring Initializr](https://start.spring.io/)

For API documentation, please check the swagger ui at http://localhost:8080/swagger-ui.html

Thanks and full credit to the awesome library https://github.com/bhlangonijr/chesslib for all of the actual chess stuff

To run the integration test suite: `mvn clean install -Pintegration`
