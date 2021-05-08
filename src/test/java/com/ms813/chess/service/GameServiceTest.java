package com.ms813.chess.service;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.entity.ChessMove;
import com.ms813.chess.exception.GameNotFoundException;
import com.ms813.chess.exception.InactiveGameException;
import com.ms813.chess.model.NewGameRequest;
import com.ms813.chess.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository repository;

    private GameService gameService;

    private ChessGame sampleGame;

    @BeforeEach
    public void setUp() {
        this.gameService = spy(new GameService(repository));
        sampleGame = new ChessGame("Hikaru Nakamura", "Garry Kasparov");
        sampleGame.setId(123L);
    }

    @Test
    public void getGame() {
        when(repository.findById(123L)).thenReturn(Optional.of(sampleGame));

        final ChessGame game = this.gameService.getGame(123L);
        assertEquals(123L, game.getId());
        assertEquals("Hikaru Nakamura", game.getWhitePlayerName());
        assertEquals("Garry Kasparov", game.getBlackPlayerName());
    }

    @Test
    public void getGameWhenIdDoesNotExist() {
        when(repository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.getGame(123L));
    }

    @Test
    public void createGame() {
        final NewGameRequest newGameRequest = new NewGameRequest("Judit Polgar", "Ju Wenjun");
        gameService.createGame(newGameRequest);
        final ArgumentCaptor<ChessGame> gameArgumentCaptor = ArgumentCaptor.forClass(ChessGame.class);

        verify(repository).save(gameArgumentCaptor.capture());

        final ChessGame capturedGameArg = gameArgumentCaptor.getValue();
        assertEquals("Judit Polgar", capturedGameArg.getWhitePlayerName());
        assertEquals("Ju Wenjun", capturedGameArg.getBlackPlayerName());
    }

    @Test
    public void resign() {
        when(repository.findById(123L)).thenReturn(Optional.of(sampleGame));

        final ChessGame whiteWins = gameService.resign(123L, Side.BLACK);
        assertEquals(1f, whiteWins.getScore());
        verify(repository).save(sampleGame);

        final ChessGame anotherSample = new ChessGame();
        when(repository.findById(456L)).thenReturn(Optional.of(anotherSample));
        final ChessGame blackWins = gameService.resign(456L, Side.WHITE);
        assertEquals(-1f, blackWins.getScore());
        verify(repository).save(anotherSample);
    }

    @Test
    public void resignButGameDoesNotExist() {
        when(repository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.resign(123L, Side.WHITE));

        verify(repository, never()).save(any(ChessGame.class));
    }

    @Test
    public void makeLegalMove() {
        final ChessGame game = spy(new ChessGame("white", "black"));
        game.setId(123);
        game.setScore(0f);
        game.setMoves(new ArrayList<>());
        doReturn(game).when(gameService).getGame(123);

        assertEquals(ChessGame.BOARD_START_POSITION_FEN, sampleGame.getCurrentPositionFEN());


        gameService.makeMove(123, "e4");

        final ArgumentCaptor<ChessGame> gameArgumentCaptor = ArgumentCaptor.forClass(ChessGame.class);
        verify(repository).save(gameArgumentCaptor.capture());

        final ChessGame savedGame = gameArgumentCaptor.getValue();

        assertEquals("e4", savedGame.getMoves().get(0).getSan());

        // white just played so should see a 0 in the database
        assertFalse(savedGame.getMoves().get(0).getActiveColour());

        assertEquals(1, savedGame.getMoves().get(0).getTurnCounter());
    }

    @Test
    public void makeMoveOnFinishedGame() {
        final ChessGame game = spy(new ChessGame("white", "black"));
        game.setId(123);
        game.setScore(1f);
        game.setMoves(Collections.emptyList());
        doReturn(game).when(gameService).getGame(123);

        assertThrows(InactiveGameException.class, () -> gameService.makeMove(123, "e4"));

        verify(game, never()).getMoves();
        verify(repository, never()).save(any(ChessGame.class));
    }

    @Test
    public void makeIllegalMove() {
        final ChessGame game = spy(new ChessGame("white", "black"));
        game.setId(123);
        game.setScore(0f);
        game.setMoves(Collections.emptyList());
        doReturn(game).when(gameService).getGame(123);

        assertThrows(MoveConversionException.class, () -> gameService.makeMove(123, "Ra8"));

        verify(game, never()).getMoves();
        verify(repository, never()).save(any(ChessGame.class));
    }

    @Test
    public void makeWinningMoveForBlack() {
        final ChessGame game = spy(new ChessGame("white", "black"));
        game.setId(123);
        game.setScore(0f);
        game.setMoves(new ArrayList<>());
        doReturn(game).when(gameService).getGame(123);

        // set up a fool's mate
        Arrays.asList(
            new ChessMove(game, "f3", 1, false),
            new ChessMove(game, "e6", 1, true),
            new ChessMove(game, "g4", 2, false),
            new ChessMove(game, "Qh4#", 2, true)
        ).forEach(
            move -> gameService.makeMove(123, move.getSan())
        );

        final ArgumentCaptor<ChessGame> gameArgumentCaptor = ArgumentCaptor.forClass(ChessGame.class);

        verify(repository, times(4)).save(gameArgumentCaptor.capture());

        final ChessGame actualGame = gameArgumentCaptor.getValue();

        assertEquals(4, actualGame.getMoves().size());
        assertEquals(-1, actualGame.getScore());
        assertEquals(
            // generated from lichess.org
            "rnb1kbnr/pppp1ppp/4p3/8/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3",
            actualGame.getCurrentPositionFEN()
        );
    }

    @Test
    public void makeWinningMoveForWhite() {
        final ChessGame game = spy(new ChessGame("white", "black"));
        game.setId(123);
        game.setScore(0f);
        game.setMoves(new ArrayList<>());
        doReturn(game).when(gameService).getGame(123);

        // set up a reverse fool's mate
        Arrays.asList(
            new ChessMove(game, "e4", 1, false),
            new ChessMove(game, "f6?", 1, true),
            new ChessMove(game, "d4", 2, false),
            new ChessMove(game, "g5??", 2, true),
            new ChessMove(game, "Qh5#", 3, false)
        ).forEach(
            move -> gameService.makeMove(123, move.getSan())
        );

        final ArgumentCaptor<ChessGame> gameArgumentCaptor = ArgumentCaptor.forClass(ChessGame.class);

        verify(repository, times(5)).save(gameArgumentCaptor.capture());

        final ChessGame actualGame = gameArgumentCaptor.getValue();

        assertEquals(5, actualGame.getMoves().size());
        assertEquals(1, actualGame.getScore());
        assertEquals(
            "rnbqkbnr/ppppp2p/5p2/6pQ/3PP3/8/PPP2PPP/RNB1KBNR b KQkq - 1 3",
            actualGame.getCurrentPositionFEN()
        );
    }

    @Test
    public void makeDrawingMove() {
        final ChessGame game = spy(new ChessGame("white", "black"));
        game.setId(123);
        game.setScore(0f);
        game.setMoves(new ArrayList<>());
        doReturn(game).when(gameService).getGame(123);

        // set up shortest known stalemate, https://www.chess.com/forum/view/game-showcase/fastest-stalemate-known-in-chess
        Arrays.asList(
            new ChessMove(game, "e3", 1, false),
            new ChessMove(game, "a5", 1, true),
            new ChessMove(game, "Qh5", 2, false),
            new ChessMove(game, "Ra6", 2, true),
            new ChessMove(game, "Qxa5", 3, false),
            new ChessMove(game, "h5", 3, true),
            new ChessMove(game, "h4", 4, false),
            new ChessMove(game, "Rah6", 4, true),
            new ChessMove(game, "Qxc7", 5, false),
            new ChessMove(game, "f6", 5, true),
            new ChessMove(game, "Qxd7+", 6, false),
            new ChessMove(game, "Kf7", 6, true),
            new ChessMove(game, "Qxb7", 7, false),
            new ChessMove(game, "Qd3", 7, true),
            new ChessMove(game, "Qxb8", 8, false),
            new ChessMove(game, "Qh7", 8, true),
            new ChessMove(game, "Qxc8", 9, false),
            new ChessMove(game, "Kg6", 9, true),
            new ChessMove(game, "Qe6", 10, false)
        ).forEach(
            move -> gameService.makeMove(123, move.getSan())
        );


        final ArgumentCaptor<ChessGame> gameArgumentCaptor = ArgumentCaptor.forClass(ChessGame.class);

        verify(repository, times(19)).save(gameArgumentCaptor.capture());

        final ChessGame actualGame = gameArgumentCaptor.getValue();

        assertEquals(19, actualGame.getMoves().size());
        assertEquals(0.5f, actualGame.getScore());
        assertEquals(
            "5bnr/4p1pq/4Qpkr/7p/7P/4P3/PPPP1PP1/RNB1KBNR b KQ - 2 10",
            actualGame.getCurrentPositionFEN()
        );
    }
}
