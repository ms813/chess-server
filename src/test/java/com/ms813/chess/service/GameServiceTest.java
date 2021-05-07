package com.ms813.chess.service;

import com.github.bhlangonijr.chesslib.Side;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.exception.GameNotFoundException;
import com.ms813.chess.model.NewGameRequest;
import com.ms813.chess.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository repository;

    private GameService gameService;

    private ChessGame sampleGame;

    @BeforeEach
    public void setUp() {
        this.gameService = new GameService(repository);
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
}
