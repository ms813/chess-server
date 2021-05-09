package com.ms813.chess.controller;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.exception.GameNotFoundException;
import com.ms813.chess.exception.InactiveGameException;
import com.ms813.chess.model.NewGameRequest;
import com.ms813.chess.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("game")
public class GameController {

    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Create a new game and return it
     *
     * @param newGameRequest data required to initialize a new game. Both player names must not be blank
     * @return the new game
     * @throws ResponseStatusException if one or both of the player's names are blank
     */
    @PostMapping
    public ChessGame createGame(@RequestBody final NewGameRequest newGameRequest) {
        if (null == newGameRequest || !newGameRequest.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create game - one or both player's names were blank!");
        }
        return this.gameService.createGame(newGameRequest);
    }

    /**
     * Get a specific game by ID
     *
     * @param id the ID of the game to return
     * @return the game with the ID specified
     * @throws GameNotFoundException if the game with the specified ID doesn't exist in the database
     */
    @GetMapping("/{id}")
    public ChessGame getGame(@PathVariable final long id) throws GameNotFoundException {
        return this.gameService.getGame(id);
    }

    /**
     * Attempt to make a move. If the move is illegal, a warning message will be returned
     *
     * @param id      the game ID on which to make the move
     * @param moveSAN a SAN string representing the move to be made
     * @return the game after the move has been made
     * @throws InactiveGameException   if trying to apply moves to a finished game
     * @throws ResponseStatusException if the supplied move is syntactically incorrect, or illegal in context of the game
     * @throws GameNotFoundException   if the supplied game ID cannot be found
     */
    @PostMapping("/{id}/move")
    public ChessGame makeMove(@PathVariable final long id, @RequestBody final String moveSAN) {
        try {
            return this.gameService.makeMove(id, moveSAN);
        } catch (final MoveConversionException e) {
            // wrap any syntactically invalid moves in a 400 response
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Bad SAN: %s. Root cause: %s", moveSAN, e.getMessage()), e);
        }
    }

    /**
     * Immediately forfeit the game so that the other player wins.
     *
     * @param id   the game ID to apply the resignation to
     * @param side the colour of the player resigning
     * @return the final game state after the resignation
     */
    @PostMapping("/{id}/resign")
    public ChessGame resign(@PathVariable final long id, @RequestBody final Side side) {
        return this.gameService.resign(id, side);
    }
}
