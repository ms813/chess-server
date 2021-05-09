package com.ms813.chess.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewGameRequest {
    private String whitePlayerName;
    private String blackPlayerName;

    public NewGameRequest() {
    }

    public NewGameRequest(final String whitePlayerName, final String blackPlayerName) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewGameRequest that = (NewGameRequest) o;
        return Objects.equals(whitePlayerName, that.whitePlayerName) && Objects.equals(blackPlayerName, that.blackPlayerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whitePlayerName, blackPlayerName);
    }

    @Override
    public String toString() {
        return "NewGameRequest{" +
            "whitePlayerName='" + whitePlayerName + '\'' +
            ", blackPlayerName='" + blackPlayerName + '\'' +
            '}';
    }

    public boolean isValid() {
        return !(whitePlayerName == null
            || blackPlayerName == null
            || whitePlayerName.isBlank()
            || blackPlayerName.isBlank());
    }
}
