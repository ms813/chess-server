package com.ms813.chess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.exception.GameNotFoundException;
import com.ms813.chess.exception.InactiveGameException;
import com.ms813.chess.model.NewGameRequest;
import com.ms813.chess.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    private ChessGame sampleGame;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        sampleGame = new ChessGame("Magnus Carlsen", "Bobby Fischer");
        sampleGame.setId(123);
    }

    @Test
    public void controllerShouldReturnGameWhenCalledWithGet() throws Exception {
        when(gameService.getGame(123)).thenReturn(sampleGame);

        this.mockMvc.perform(get("/game/123"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("123"))
            .andExpect(jsonPath("$.whitePlayerName").value("Magnus Carlsen"))
            .andExpect(jsonPath("$.blackPlayerName").value("Bobby Fischer"));

        verify(gameService).getGame(123);
    }

    @Test
    public void getIsNotSupportedForCreatingNewGames() throws Exception {
        this.mockMvc.perform(get("/game"))
            .andExpect(status().is4xxClientError());

        verifyNoInteractions(gameService);
    }


    @Test
    public void getNonExistentGame() throws Exception {
        when(gameService.getGame(123))
            .thenThrow(new GameNotFoundException("game not found"));

        this.mockMvc.perform(get("/game/123"))
            .andExpect(status().isNotFound());

        verify(gameService).getGame(123);
    }

    @Test
    public void createNewGameWhenCalledWithPost() throws Exception {
        final NewGameRequest newGameRequest = new NewGameRequest("Magnus Carlsen", "Bobby Fischer");
        final String newGameRequestJson = mapper.writeValueAsString(newGameRequest);

        when(gameService.createGame(newGameRequest)).thenReturn(sampleGame);

        this.mockMvc.perform(post("/game")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newGameRequestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("123"))
            .andExpect(jsonPath("$.whitePlayerName").value("Magnus Carlsen"))
            .andExpect(jsonPath("$.blackPlayerName").value("Bobby Fischer"));
        ;

        verify(gameService).createGame(newGameRequest);
    }

    @Test
    public void createNewGameFailsIfWhitePlayerNameIsNull() throws Exception {
        final NewGameRequest newGameRequest = new NewGameRequest(null, "Bobby Fischer");
        final String newGameRequestJson = mapper.writeValueAsString(newGameRequest);

        this.mockMvc.perform(post("/game")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newGameRequestJson))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(gameService);
    }

    @Test
    public void createNewGameFailsIfRequestIsNull() throws Exception {
        final String newGameRequestJson = mapper.writeValueAsString(null);
        this.mockMvc.perform(post("/game")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newGameRequestJson))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(gameService);
    }

    @Test
    public void createNewGameFailsIfWhitePlayerNameIsEmpty() throws Exception {
        final NewGameRequest newGameRequest = new NewGameRequest("", "Bobby Fischer");
        final String newGameRequestJson = mapper.writeValueAsString(newGameRequest);

        this.mockMvc.perform(post("/game")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newGameRequestJson))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(gameService);
    }

    @Test
    public void createNewGameFailsIfWhitePlayerNameIsOnlyWhitespace() throws Exception {

        final NewGameRequest newGameRequest = new NewGameRequest(" \n\t\r", "Bobby Fischer");
        final String newGameRequestJson = mapper.writeValueAsString(newGameRequest);

        this.mockMvc.perform(post("/game")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newGameRequestJson))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(gameService);
    }

    @Test
    public void resign() throws Exception {
        this.mockMvc.perform(post("/game/123/resign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Side.WHITE)))
            .andExpect(status().isOk());

        verify(gameService).resign(123, Side.WHITE);
    }

    @Test
    public void resignGameDoesNotExist() throws Exception {
        when(gameService.resign(123, Side.WHITE))
            .thenThrow(new GameNotFoundException("game not found"));

        this.mockMvc.perform(post("/game/123/resign")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Side.WHITE)))
            .andExpect(status().isNotFound());

        verify(gameService).resign(123, Side.WHITE);
    }

    @Test
    public void makeLegalMove() throws Exception {
        final String move = "e4";
        when(gameService.makeMove(123, move)).thenReturn(sampleGame);

        this.mockMvc.perform(post("/game/123/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(move))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("123"))
            .andExpect(jsonPath("$.whitePlayerName").value("Magnus Carlsen"))
            .andExpect(jsonPath("$.blackPlayerName").value("Bobby Fischer"));

        verify(gameService).makeMove(123, move);
    }

    @Test
    public void makeIllegalMove() throws Exception {
        final String move = "Kd4";
        when(gameService.makeMove(123, move)).thenThrow(new InactiveGameException("illegal move"));

        this.mockMvc.perform(post("/game/123/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(move))
            .andExpect(status().isUnprocessableEntity());

        verify(gameService).makeMove(123, move);
    }

    @Test
    public void makeSyntacticallyIncorrectMove() throws Exception {
        final String move = "this isn't a valid SAN";

        when(gameService.makeMove(123, move)).thenThrow(new MoveConversionException("invalid move"));

        this.mockMvc.perform(post("/game/123/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(move))
            .andExpect(status().isBadRequest());

        verify(gameService).makeMove(123, move);
    }

    @Test
    public void makeMoveGameDoesNotExist() throws Exception {
        final String move = "e4";
        when(gameService.makeMove(123, move)).thenThrow(new GameNotFoundException("game not found"));

        this.mockMvc.perform(post("/game/123/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(move))
            .andExpect(status().isNotFound());

        verify(gameService).makeMove(123, move);
    }
}
