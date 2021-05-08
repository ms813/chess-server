package com.ms813.chess.it;

import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.model.NewGameRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ExtractingResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CucumberStepDefs extends SpringIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(CucumberStepDefs.class);

    @Autowired
    private ServletWebServerApplicationContext webServerApplicationContext;

    private RestTemplate restTemplate;
    private final String host = "localhost";
    private int port;
    private ResponseEntity<ChessGame> response;
    private HttpStatus responseStatus;

    // long to match type of incoming game IDs from database
    private long gameIdCounter = 0;

    @Before
    public void setUp() {
        port = webServerApplicationContext.getWebServer().getPort();
        restTemplate = new RestTemplateBuilder().errorHandler(new ExtractingResponseErrorHandler()).build();
        response = null;
        responseStatus = null;
    }

    @When("the game endpoint is called with POST with white player name {string} and black player name {string}")
    public void theGameEndpointIsCalledWithPostWithPlayerNames(final String whitePlayerName, final String blackPlayerName) {
        final NewGameRequest newGameRequest = new NewGameRequest(whitePlayerName, blackPlayerName);

        final String url = String.format("http://%s:%d/game", host, port);

        logger.info("Calling {} with {}", url, newGameRequest);
        try {
            this.response = restTemplate.postForEntity(url, newGameRequest, ChessGame.class);
            ++gameIdCounter;
            logger.info("Received '{}' response, body: {}", response.getStatusCode(), response.getBody());
        } catch (HttpClientErrorException.BadRequest e) {
            // expected when game fails to create because of intentional bad input
            // this workaround is required since the postForEntity above fails to complete in the case of non-200 responses
            responseStatus = e.getStatusCode();
            logger.info("Received '400' response");
        }
    }

    @Then("a 200 response should be returned")
    public void a200ResponseShouldBeReturned() {
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Then("a 400 response should be returned")
    public void a400ResponseShouldBeReturned() {
        assertThat(responseStatus, equalTo(HttpStatus.BAD_REQUEST));
    }

    @And("the game is returned")
    public void theNewGameIsReturned() {
        assertThat("Returned game ID does not match expected game ID", response.getBody().getId(), equalTo(gameIdCounter));
    }
}
