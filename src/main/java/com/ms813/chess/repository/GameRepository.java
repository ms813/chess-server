package com.ms813.chess.repository;

import com.ms813.chess.entity.ChessGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameRepository extends JpaRepository<ChessGame, Long> {
}
