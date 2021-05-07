package com.ms813.chess.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms813.chess.entity.ChessGame;
import com.ms813.chess.model.NewGameRequest;
import com.ms813.chess.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Same;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.SampleModel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
            .andExpect(jsonPath("$.blackPlayerName").value("Bobby Fischer"));;

        verify(gameService).createGame(newGameRequest);
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
}
