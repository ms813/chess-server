package com.ms813.chess;

import com.github.bhlangonijr.chesslib.Side;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.model.NewGameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Helper for post/put requests during application development
 */
public class LocalTestUtil {

    private static final Logger logger = LoggerFactory.getLogger(LocalTestUtil.class);
    private static final String url = "http://localhost:8080/game";

    private RestTemplate restTemplate;

    public static void main(String[] args) {
        LocalTestUtil localTestUtil = new LocalTestUtil();

        final List<String> moves = Arrays.asList(
            "e4", "e5", "Nc3",
            //invalid
            "Ra1",
            // valid move for black, should still be ply 4
            "h5"
        );
        ChessGame game = localTestUtil.createGame();

        for (String move : moves) {
            game = localTestUtil.makeMove(game.getId(), move);
            logger.info("Got game after move {}", move);
            logger.info("\n{}", game.getBoardFromWhiteViewPoint());
        }
        localTestUtil.resign(game.getId(), Side.WHITE);

        // not allowed since game has ended
        localTestUtil.makeMove(game.getId(), "a1");

    }

    public LocalTestUtil() {
        restTemplate = new RestTemplate();
    }

    public ChessGame getGame(final long gameId) {
        final ResponseEntity<ChessGame> response = restTemplate.getForEntity(String.format("%s/%d", url, gameId), ChessGame.class);
        return response.getBody();
    }

    public ChessGame createGame() {

        final NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setWhitePlayerName("Test White Player");
        newGameRequest.setBlackPlayerName("Test Black Player");


        final ResponseEntity<ChessGame> response = restTemplate.postForEntity(url, newGameRequest, ChessGame.class);

        logger.info("Got new game: {}", response.getBody());
        return response.getBody();
    }

    public ChessGame makeMove(final long gameId, final String moveSAN) {
        final String moveUrl = String.format("%s/%d/move", url, gameId);
        try {
            return restTemplate.postForEntity(moveUrl, moveSAN, ChessGame.class).getBody();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return this.getGame(gameId);
        }
    }

    public void resign(long id, Side side) {
        restTemplate.postForEntity(url + "/" + id + "/resign", side, ChessGame.class);
    }
}
