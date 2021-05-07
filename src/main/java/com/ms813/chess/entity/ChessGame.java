package com.ms813.chess.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.bhlangonijr.chesslib.Board;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

// avoids circular dependency during serialization
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class ChessGame {

    // represents the standard starting position of the pieces on the board
    public static final String BOARD_START_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private long id;

    private String whitePlayerName;
    private String blackPlayerName;

    // compact string notation representing the board state after the last turn
    private String currentPositionFEN = BOARD_START_POSITION_FEN;

    private Timestamp startedTimestamp;

    private List<ChessMove> moves;

    // 1 for a white win, 0.5 for a draw, 0 if the game has not concluded -1 for a black win
    private float score;

    public ChessGame() {
    }

    public ChessGame(final String whitePlayerName, final String blackPlayerName) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName = whitePlayerName;
    }

    @Column(nullable = false)
    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
    }

    @CreationTimestamp
    @Column
    public Timestamp getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Timestamp startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    public List<ChessMove> getMoves() {
        return moves;
    }

    public void setMoves(List<ChessMove> moves) {
        this.moves = moves;
    }

    @Column
    public String getCurrentPositionFEN() {
        return currentPositionFEN;
    }

    public void setCurrentPositionFEN(String currentPositionFEN) {
        this.currentPositionFEN = currentPositionFEN;
    }

    @Column
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Transient
    @JsonIgnore
    public String getBoardFromWhiteViewPoint() {
        final Board board = new Board();
        board.loadFromFen(currentPositionFEN);
        return board.toStringFromWhiteViewPoint();
    }

    @Transient
    @JsonIgnore
    public String getBoardFromBlackViewPoint() {
        final Board board = new Board();
        board.loadFromFen(currentPositionFEN);
        return board.toStringFromBlackViewPoint();
    }
}
