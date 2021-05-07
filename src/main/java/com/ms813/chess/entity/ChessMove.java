package com.ms813.chess.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;

//avoids circular dependency during serialization
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@Entity
public class ChessMove {

    private long id;

    private ChessGame game;

    private Timestamp playedAtTimestamp;

    // compact notation uniquely identifying a move
    private String san;

    // false if white's move, true if black's.
    // Represented as 0 or 1 respectively in FEN notation
    private boolean activeColour;

    // First turn is 1
    private int turnCounter;

    public ChessMove() {
    }

    public ChessMove(final ChessGame game, final String san, final int turnCounter, final boolean activeColour) {
        this.game = game;
        this.san = san;
        this.turnCounter = turnCounter;
        this.activeColour = activeColour;
    }

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Column
    public String getSan() {
        return san;
    }

    public void setSan(String moveSAN) {
        this.san = moveSAN;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column
    @CreationTimestamp
    public Timestamp getPlayedAtTimestamp() {
        return playedAtTimestamp;
    }

    public void setPlayedAtTimestamp(Timestamp playedAtTimestamp) {
        this.playedAtTimestamp = playedAtTimestamp;
    }

    @Column
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean getActiveColour() {
        return activeColour;
    }

    public void setActiveColour(boolean moveOrder) {
        this.activeColour = moveOrder;
    }

    @Column
    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }
}
