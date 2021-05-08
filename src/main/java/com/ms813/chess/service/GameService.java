package com.ms813.chess.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.entity.ChessMove;
import com.ms813.chess.exception.GameNotFoundException;
import com.ms813.chess.exception.InactiveGameException;
import com.ms813.chess.model.NewGameRequest;
import com.ms813.chess.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public ChessGame getGame(final long gameId) {
        return this.gameRepository.findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(String.format("Could not find game with ID %s", gameId)));
    }

    public ChessGame createGame(final NewGameRequest newGameRequest) {
        final ChessGame chessGame = new ChessGame(
            newGameRequest.getWhitePlayerName(),
            newGameRequest.getBlackPlayerName()
        );

        return this.gameRepository.save(chessGame);
    }

    public ChessGame makeMove(final long gameId, final String moveSAN) {
        final ChessGame game = this.getGame(gameId);

        if (game.getScore() != 0) {
            // game has already ended, no more moves can be made
            throw new InactiveGameException(String.format("Game %s is finished, no more moves can be made", gameId));
        }

        final Board board = new Board();
        board.loadFromFen(game.getCurrentPositionFEN());
        final int moveCounter = board.getMoveCounter();

        final boolean sideToMove = board.getSideToMove() == Side.BLACK;

        // this throws a MoveConversionException if the move is illegal
        board.doMove(moveSAN);

        if (board.isMated()) {
            // game is over, the player who made the last move was victorious
            game.setScore(sideToMove ? -1f : 1f);
        } else if (board.isDraw()) {
            // the game has ended in a draw
            game.setScore(0.5f);
        }

        // move was legal and has been applied to the board, save it to the database
        final ChessMove chessMove = new ChessMove(game, moveSAN, moveCounter, sideToMove);
        game.getMoves().add(chessMove);
        game.setCurrentPositionFEN(board.getFen());

        this.gameRepository.save(game);

        return game;
    }

    public ChessGame resign(final long id, final Side side) {
        final ChessGame game = this.getGame(id);

        // the game is over, the player who resigned loses
        game.setScore(side == Side.BLACK ? 1f : -1f);

        this.gameRepository.save(game);

        return game;
    }
}
