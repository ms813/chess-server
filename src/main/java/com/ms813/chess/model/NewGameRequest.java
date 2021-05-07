package com.ms813.chess.model;

public class NewGameRequest {
    private String whitePlayerName;
    private String blackPlayerName;
    private String startingFEN;

    // required for deserialization
    public NewGameRequest() {}

    public NewGameRequest(String whitePlayerName, String blackPlayerName) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName = whitePlayerName;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
    }

    public String getStartingFEN() {
        return startingFEN;
    }

    public void setStartingFEN(String startingFEN) {
        this.startingFEN = startingFEN;
    }
}
